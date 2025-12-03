package com.gbr.data.repository

import java.io.File

interface FileRepository {
    suspend fun downloadAndUnzip(
        url: String,
        destinationDir: File
    ): Result<File>
}

