package com.gbr.datasource

import java.io.File

interface LocalFileDataSource {
    suspend fun unzip(zipFile: File, destinationDir: File): Result<File>
}

