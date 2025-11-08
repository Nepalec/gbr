package com.gbr.data.repository

import com.gbr.model.book.ImageFileItem
import com.gbr.model.gitabase.GitabaseID
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing image file operations.
 * Provides methods to check if image files are extracted to the device.
 */
interface ImageFilesRepository {
    /**
     * Checks if all image files in the list exist in the app's internal storage.
     *
     * @param gitabaseId The Gitabase ID to determine the folder path
     * @param imagefiles List of image files to check
     * @return Flow<Boolean> that emits true if all files exist, false otherwise
     */
    suspend fun checkImageFilesExtracted(
        gitabaseId: GitabaseID,
        imagefiles: List<ImageFileItem>
    ): Flow<Boolean>
}



