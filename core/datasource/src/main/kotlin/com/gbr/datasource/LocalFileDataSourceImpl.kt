package com.gbr.datasource

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.inject.Inject

class LocalFileDataSourceImpl @Inject constructor() : LocalFileDataSource {

    override suspend fun unzip(zipFile: File, destinationDir: File): Result<File> = withContext(Dispatchers.IO) {
        runCatching {
            if (!destinationDir.exists()) {
                destinationDir.mkdirs()
            }

            ZipInputStream(FileInputStream(zipFile)).use { zis ->
                var entry: ZipEntry?
                val buffer = ByteArray(8192)

                while (zis.nextEntry.also { entry = it } != null) {
                    entry?.let { ze ->
                        val file = File(destinationDir, ze.name)

                        if (ze.isDirectory) {
                            file.mkdirs()
                        } else {
                            file.parentFile?.mkdirs()
                            FileOutputStream(file).use { fos ->
                                var count: Int
                                while (zis.read(buffer).also { count = it } != -1) {
                                    fos.write(buffer, 0, count)
                                }
                            }
                        }
                    }
                }
            }

            destinationDir
        }
    }
}

