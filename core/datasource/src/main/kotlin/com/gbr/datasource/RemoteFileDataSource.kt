package com.gbr.datasource

import java.io.File

interface RemoteFileDataSource {
    suspend fun downloadFile(url: String, destFile: File): Result<Unit>
    suspend fun downloadFile(url: String, destFile: File, onProgress: suspend (Long, Long) -> Unit): Result<Unit>
}

