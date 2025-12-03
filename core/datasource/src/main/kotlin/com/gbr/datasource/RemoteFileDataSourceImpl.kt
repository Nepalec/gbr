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

    override suspend fun downloadFile(url: String, destFile: File): Result<Unit> {
        return downloadFile(url, destFile) { _, _ -> }
    }

    override suspend fun downloadFile(
        url: String,
        destFile: File,
        onProgress: suspend (Long, Long) -> Unit
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                error("Download failed with code: ${response.code}")
            }

            val body = response.body ?: error("Response body is null")

            val contentLength = body.contentLength()
            val input = body.byteStream()

            FileOutputStream(destFile).use { output ->
                val buffer = ByteArray(8 * 1024)
                var totalBytesRead = 0L
                var bytesRead: Int
                var fakeProgress = 0L

                while (input.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                    totalBytesRead += bytesRead

                    if (contentLength > 0) {
                        // NORMAL MODE — real percentage
                        onProgress(totalBytesRead, contentLength)
                    } else {
                        // FALLBACK MODE — smooth fake progress
                        fakeProgress += bytesRead
                        onProgress(fakeProgress, 100_000L) // fake "total"
                    }
                }
            }

        }
    }


}

