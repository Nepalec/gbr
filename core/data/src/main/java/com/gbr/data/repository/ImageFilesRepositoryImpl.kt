package com.gbr.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.gbr.model.book.ImageFileItem
import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.gitabase.ImageFormat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ImageFilesRepository.
 * Checks if image files exist in the app's internal storage.
 */
@Singleton
class ImageFilesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ImageFilesRepository {

    override suspend fun checkImageFilesExtracted(
        gitabaseId: GitabaseID,
        imagefiles: List<ImageFileItem>
    ): Flow<Boolean> = flow {
        if (imagefiles.isEmpty()) {
            emit(true)
            return@flow
        }

        // Get app internal folder
        val appInternalFolder = context.filesDir

        // Create folder path: app_internal_folder/gitabaseId.key
        val gitabaseFolder = File(appInternalFolder, gitabaseId.key)

        // Check if all files exist and have size > 0
        val allFilesExist = imagefiles.all { imageFile ->
            val fileName = "${imageFile.id}.${imageFile.format.fileExtension}"
            val file = File(gitabaseFolder, fileName)
            file.exists() && file.length() > 0
        }

        // If all files exist, emit true and finish
        if (allFilesExist) {
            emit(true)
            return@flow
        }

        // Otherwise, check if all bitmaps are null
        val allBitmapsNull = imagefiles.all { it.bitmap == null }
        if (allBitmapsNull) {
            emit(false)
            return@flow
        }

        // Create all files by saving bitmaps
        // Ensure directory exists
        if (!gitabaseFolder.exists()) {
            gitabaseFolder.mkdirs()
        }

        var allFilesCreated = true
        imagefiles.forEach { imageFile ->
            val fileName = "${imageFile.id}.${imageFile.format.fileExtension}"
            val file = File(gitabaseFolder, fileName)

            // Skip if file already exists and has size > 0
            if (file.exists() && file.length() > 0) {
                return@forEach
            }

            // If bitmap is null, skip this file
            val bitmap = imageFile.bitmap
            if (bitmap == null) {
                allFilesCreated = false
                return@forEach
            }

            try {
                if (imageFile.format == ImageFormat.SVG) {
                    // For SVG: Decode Base64 to get SVG string, then write as text
                    val svgContent = String(Base64.decode(bitmap, Base64.DEFAULT), Charsets.UTF_8)
                    FileOutputStream(file).use { fileOutputStream ->
                        OutputStreamWriter(fileOutputStream, Charsets.UTF_8).use { writer ->
                            writer.append(svgContent)
                        }
                    }
                } else {
                    // For JPEG/PNG/GIF: Decode Base64 → Bitmap → Compress JPEG → Write
                    val bitmapBytes = Base64.decode(bitmap, Base64.DEFAULT)
                    val decodedBitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.size)

                    if (decodedBitmap == null) {
                        allFilesCreated = false
                        return@forEach
                    }

                    FileOutputStream(file).use { outputStream ->
                        decodedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    }
                }
            } catch (e: Exception) {
                // If any file fails to save, mark as not all created
                allFilesCreated = false
            }
        }

        emit(allFilesCreated)
    }
}

