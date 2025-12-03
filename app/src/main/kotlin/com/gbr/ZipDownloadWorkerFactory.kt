package com.gbr

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.gbr.datasource.RemoteFileDataSource
import com.gbr.work.ZipDownloadWorker
import javax.inject.Inject

class ZipDownloadWorkerFactory @Inject constructor(
    private val remoteFileDataSource: RemoteFileDataSource
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            ZipDownloadWorker::class.java.name -> {
                ZipDownloadWorker(
                    appContext,
                    workerParameters,
                    remoteFileDataSource
                )
            }
            else -> null
        }
    }
}

