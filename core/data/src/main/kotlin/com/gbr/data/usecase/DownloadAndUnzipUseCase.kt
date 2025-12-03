package com.gbr.data.usecase

import com.gbr.data.repository.FileRepository
import java.io.File
import javax.inject.Inject

class DownloadAndUnzipUseCase @Inject constructor(
    private val fileRepository: FileRepository
) {
    suspend operator fun invoke(url: String, destinationDir: File): Result<File> {
        return fileRepository.downloadAndUnzip(url, destinationDir)
    }
}

