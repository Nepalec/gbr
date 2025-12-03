package com.gbr.data.usecase

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.workDataOf
import com.gbr.model.work.DownloadWorkConstants
import com.gbr.work.ZipDownloadWorker
import javax.inject.Inject

class CreateDownloadWorkRequestUseCase @Inject constructor() {
    operator fun invoke(
        downloadUrl: String,
        destinationPath: String
    ): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<ZipDownloadWorker>()
            .setInputData(
                workDataOf(
                    DownloadWorkConstants.KEY_FILE_URL to downloadUrl,
                    DownloadWorkConstants.KEY_DESTINATION_PATH to destinationPath
                )
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
    }
}

