package com.gbr.data.repository

import com.gbr.datasource.LocalFileDataSource
import com.gbr.datasource.RemoteFileDataSource
import java.io.File
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(
    private val remoteFileDataSource: RemoteFileDataSource,
    private val localFileDataSource: LocalFileDataSource
) : FileRepository {

    override suspend fun downloadAndUnzip(url: String, destinationDir: File): Result<File> {
        val tempZip = File(destinationDir.parentFile ?: destinationDir, "temp.zip")

        return remoteFileDataSource.downloadFile(url, tempZip)
            .mapCatching {
                val result = localFileDataSource.unzip(tempZip, destinationDir).getOrThrow()
                tempZip.delete()
                result
            }
    }
}

