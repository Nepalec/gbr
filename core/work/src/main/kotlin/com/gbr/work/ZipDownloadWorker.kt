package com.gbr.work

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.gbr.data.usecase.ScanGitabaseFilesUseCase
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
    @Assisted private val scanGitabaseFilesUseCase: ScanGitabaseFilesUseCase,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.e("ZipDownloadWorker", "=== doWork() STARTED ===")

        ensureNotificationChannel()

        val url = inputData.getString(DownloadWorkConstants.KEY_FILE_URL) ?: return Result.failure()
        val destinationPath = inputData.getString(DownloadWorkConstants.KEY_DESTINATION_PATH) ?: return Result.failure()
        val gitabaseId = inputData.getString(DownloadWorkConstants.KEY_GITABASE_ID)

        if (gitabaseId != null) {
            setProgress(workDataOf(DownloadWorkConstants.KEY_GITABASE_ID to gitabaseId))
        }

        val destDir = File(destinationPath).apply { if (!exists()) mkdirs() }
        val tempZip = File(applicationContext.cacheDir, "temp.zip")

        // Start as foreground right away
        setForeground(createForegroundInfo(0, DownloadStage.STARTING))

        return try {
            // Download stage: 0–80%
            downloadFileWithProgress(url, tempZip, gitabaseId)

            // Unzip stage: 80–100%
            unzipWithProgress(tempZip, destDir, gitabaseId)

            tempZip.delete()

            addDownloadedGitabasesToRepository(destDir)

            // Optional: show "completed" for a moment
            setForeground(createForegroundInfo(100, DownloadStage.COMPLETED))
            setProgress(
                workDataOf(
                    DownloadWorkConstants.KEY_PROGRESS to 100,
                    DownloadWorkConstants.KEY_STAGE to DownloadStage.COMPLETED.name,
                    DownloadWorkConstants.KEY_GITABASE_ID to gitabaseId
                )
            )

            Result.success()
        } catch (e: kotlinx.coroutines.CancellationException) {
            Result.failure()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private suspend fun downloadFileWithProgress(
        url: String,
        destFile: File,
        gitabaseId: String?
    ) {
        Log.e("ZipDownloadWorker", "=== downloadFileWithProgress() STARTED ===")

        var lastProgress = 0

        // Switch notification to "Downloading" ONCE
        setForeground(createForegroundInfo(0, DownloadStage.DOWNLOADING))

        setProgress(
            workDataOf(
                DownloadWorkConstants.KEY_PROGRESS to 0,
                DownloadWorkConstants.KEY_STAGE to DownloadStage.DOWNLOADING.name,
                DownloadWorkConstants.KEY_GITABASE_ID to gitabaseId
            )
        )

        val result = remoteFileDataSource.downloadFile(url, destFile) { bytesRead, totalBytes ->
            // Map download progress (0–100) to global 0–80
            val downloadPercent = if (totalBytes > 0) {
                ((bytesRead * 100f) / totalBytes).toInt().coerceIn(0, 100)
            } else {
                // fallback if totalBytes is unknown
                (lastProgress + 1).coerceAtMost(80)
            }

            val globalProgress = (downloadPercent * 0.8f).toInt() // 0–80

            if (globalProgress != lastProgress) {
                lastProgress = globalProgress

                setProgress(
                    workDataOf(
                        DownloadWorkConstants.KEY_PROGRESS to globalProgress,
                        DownloadWorkConstants.KEY_STAGE to DownloadStage.DOWNLOADING.name,
                        DownloadWorkConstants.KEY_GITABASE_ID to gitabaseId
                    )
                )
            }
        }

        if (result.isFailure) {
            throw result.exceptionOrNull() ?: Exception("Download failed")
        }

        // Ensure we finish at 80
        setProgress(
            workDataOf(
                DownloadWorkConstants.KEY_PROGRESS to 80,
                DownloadWorkConstants.KEY_STAGE to DownloadStage.DOWNLOADING.name,
                DownloadWorkConstants.KEY_GITABASE_ID to gitabaseId
            )
        )
    }

    private suspend fun unzipWithProgress(
        zipFile: File,
        destinationDir: File,
        gitabaseId: String?
    ) {
        Log.e("ZipDownloadWorker", "=== unzipWithProgress() STARTED ===")

        val totalEntries = countEntries(zipFile)
        var current = 0
        var lastProgress = 80 // we continue from 80

        // Switch notification to "Unzipping" ONCE
        setForeground(createForegroundInfo(lastProgress, DownloadStage.UNZIPPING))

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
                // Map unzip progress to 80–100
                val unzipPercent = ((current * 100f) / totalEntries).toInt().coerceIn(0, 100)
                val globalProgress = 80 + (unzipPercent * 0.2f).toInt() // 80–100

                if (globalProgress != lastProgress) {
                    lastProgress = globalProgress
                    Log.e("ZipDownloadWorker", "=== unzipWithProgress() progress:$globalProgress ===")

                    setProgress(
                        workDataOf(
                            DownloadWorkConstants.KEY_PROGRESS to globalProgress,
                            DownloadWorkConstants.KEY_STAGE to DownloadStage.UNZIPPING.name,
                            DownloadWorkConstants.KEY_GITABASE_ID to gitabaseId
                        )
                    )
                }
            }
        }

        // Ensure 100% is reported
        setProgress(
            workDataOf(
                DownloadWorkConstants.KEY_PROGRESS to 100,
                DownloadWorkConstants.KEY_STAGE to DownloadStage.UNZIPPING.name,
                DownloadWorkConstants.KEY_GITABASE_ID to gitabaseId
            )
        )
    }

    private fun countEntries(zipFile: File): Int {
        ZipFile(zipFile).use { zip ->
            return zip.entries().asSequence().count().coerceAtLeast(1)
        }
    }

    private suspend fun addDownloadedGitabasesToRepository(destinationDir: File) {
        try {
            val scanResult = scanGitabaseFilesUseCase.execute(destinationDir.absolutePath)

            if (scanResult.isSuccess) {
                val gitabases = scanResult.getOrThrow()
                Log.d("ZipDownloadWorker", "Added ${gitabases.size} gitabase(s) to repository via scanning")
            } else {
                Log.w("ZipDownloadWorker", "Failed to scan and add gitabases: ${scanResult.exceptionOrNull()?.message}")
            }
        } catch (e: Exception) {
            Log.w("ZipDownloadWorker", "Failed to add downloaded gitabases to repository: ${e.message}")
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
            ForegroundInfo(notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(notificationId, notification)
        }
    }

    private fun ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                as android.app.NotificationManager

            val channel = android.app.NotificationChannel(
                "download_channel",
                "Downloads",
                android.app.NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }
    }
}
