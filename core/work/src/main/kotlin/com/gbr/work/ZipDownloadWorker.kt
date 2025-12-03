package com.gbr.work

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.gbr.datasource.RemoteFileDataSource
import com.gbr.model.download.DownloadStage
import com.gbr.model.work.DownloadWorkConstants
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.ensureActive
import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream


@HiltWorker
class ZipDownloadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @Assisted private val remoteFileDataSource: RemoteFileDataSource,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        android.util.Log.e("ZipDownloadWorker", "=== doWork() STARTED ===")
        val url = inputData.getString(DownloadWorkConstants.KEY_FILE_URL) ?: return Result.failure()
        val destinationPath = inputData.getString(DownloadWorkConstants.KEY_DESTINATION_PATH) ?: return Result.failure()

        val destDir = File(destinationPath)
        if (!destDir.exists()) {
            destDir.mkdirs()
        }

        try {
            setForegroundAsync(createForegroundInfo(0, DownloadStage.STARTING))
        } catch (e: Exception) {
            android.util.Log.w("ZipDownloadWorker", "Failed to set foreground: ${e.message}")
            // Continue without foreground notification if not allowed
        }

        val tempZip = File(applicationContext.cacheDir, "temp.zip")

        return try {
            downloadFileWithProgress(url, tempZip)
            unzipWithProgress(tempZip, destDir)
            tempZip.delete()

            try {
                setForeground(createForegroundInfo(100, DownloadStage.COMPLETED))
            } catch (e: Exception) {
                android.util.Log.w("ZipDownloadWorker", "Failed to set foreground: ${e.message}")
            }
            Result.success()
        } catch (e: kotlinx.coroutines.CancellationException) {
            Result.failure()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private suspend fun downloadFileWithProgress(url: String, destFile: File) {
        android.util.Log.e("ZipDownloadWorker", "=== downloadFileWithProgress() STARTED ===")
        val data = workDataOf(
            DownloadWorkConstants.KEY_PROGRESS to 0,
            DownloadWorkConstants.KEY_STAGE to DownloadStage.DOWNLOADING.name
        )
        setProgress(data)
        try {
            setForeground(createForegroundInfo(0, DownloadStage.DOWNLOADING))
        } catch (e: Exception) {
            android.util.Log.w("ZipDownloadWorker", "Failed to set foreground: ${e.message}")
        }

        val result = remoteFileDataSource.downloadFile(url, destFile)
        if (result.isFailure) {
            throw result.exceptionOrNull() ?: Exception("Download failed")
        }

        val completeData = workDataOf(
            DownloadWorkConstants.KEY_PROGRESS to 100,
            DownloadWorkConstants.KEY_STAGE to DownloadStage.DOWNLOADING.name
        )
        setProgress(completeData)
        try {
            setForeground(createForegroundInfo(100, DownloadStage.DOWNLOADING))
        } catch (e: Exception) {
            android.util.Log.w("ZipDownloadWorker", "Failed to set foreground: ${e.message}")
        }
    }

    private suspend fun unzipWithProgress(zipFile: File, destinationDir: File) {
        android.util.Log.e("ZipDownloadWorker", "=== unzipWithProgress() STARTED ===")
        val totalEntries = countEntries(zipFile)
        var current = 0

        ZipInputStream(FileInputStream(zipFile)).use { zis ->
            var entry: ZipEntry?
            val buffer = ByteArray(8192)

            while (zis.nextEntry.also { entry = it } != null) {
                coroutineContext.ensureActive()

                val file = File(destinationDir, entry!!.name)

                if (entry!!.isDirectory) {
                    file.mkdirs()
                } else {
                    file.parentFile?.mkdirs()
                    java.io.FileOutputStream(file).use { fos ->
                        var count: Int
                        while (zis.read(buffer).also { count = it } != -1) {
                            fos.write(buffer, 0, count)
                        }
                    }
                }

                current++
                val progress = ((current * 100f) / totalEntries).toInt()
                android.util.Log.e("ZipDownloadWorker", "=== unzipWithProgress() progress:$progress ===")
                val data = workDataOf(
                    DownloadWorkConstants.KEY_PROGRESS to progress,
                    DownloadWorkConstants.KEY_STAGE to DownloadStage.UNZIPPING.name
                )
                setProgress(data)
                try {
                    setForeground(createForegroundInfo(progress, DownloadStage.UNZIPPING))
                } catch (e: Exception) {
                    android.util.Log.w("ZipDownloadWorker", "Failed to set foreground: ${e.message}")
                }
            }
        }
    }

    private fun countEntries(zipFile: File): Int {
        ZipFile(zipFile).use { zip ->
            return zip.entries().asSequence().count().coerceAtLeast(1)
        }
    }

    private fun createForegroundInfo(progress: Int, stage: DownloadStage): ForegroundInfo {
        val channelId = "download_channel"
        val notificationId = 1

        val cancelIntent = Intent(applicationContext, CancelDownloadReceiver::class.java).apply {
            putExtra("work_id", id.toString())
        }

        val cancelPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val cancelAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_menu_close_clear_cancel,
            "Cancel",
            cancelPendingIntent
        ).build()

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(stage.name.lowercase().replaceFirstChar { it.uppercase() })
            .setContentText("Progress: $progress%")
            .setOnlyAlertOnce(true)
            .setOngoing(stage != DownloadStage.COMPLETED)
            .addAction(cancelAction)
            .setProgress(100, progress, false)
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+ requires foreground service type
            // Use dataSync for file downloads/sync operations
            ForegroundInfo(notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(notificationId, notification)
        }
    }

}

