package com.gbr.datasource

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class RemoteFileDataSourceImpl @Inject constructor(
    private val client: OkHttpClient
) : RemoteFileDataSource {

    override suspend fun downloadFile(url: String, destFile: File): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                error("Download failed with code: ${response.code}")
            }

            response.body?.byteStream()?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            } ?: error("Response body is null")
            Unit
        }
    }
}

