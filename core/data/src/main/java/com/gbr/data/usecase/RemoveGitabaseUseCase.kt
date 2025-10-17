package com.gbr.data.usecase

import android.content.Context
import com.gbr.model.gitabase.GitabaseID
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

/**
 * Use case for removing a Gitabase file from the app's gitabase folder.
 * Deletes the physical file and updates the repository.
 */
class RemoveGitabaseUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Removes a Gitabase file from the gitabase folder.
     *
     * @param gitabaseId The ID of the gitabase to remove
     * @return Result indicating success or failure
     */
    suspend fun execute(gitabaseId: GitabaseID): Result<Unit> {
        return try {
            val gitabasesFolder = File(context.getExternalFilesDir(null), "gitabases")

            if (!gitabasesFolder.exists()) {
                return Result.failure(IllegalArgumentException("Gitabases folder does not exist"))
            }

            // Construct the expected filename based on gitabase ID
            val fileName = "gitabase_${gitabaseId.key}.db"
            val fileToDelete = File(gitabasesFolder, fileName)

            if (!fileToDelete.exists()) {
                return Result.failure(IllegalArgumentException("Gitabase file does not exist: $fileName"))
            }

            if (!fileToDelete.isFile) {
                return Result.failure(IllegalArgumentException("Path is not a file: $fileName"))
            }

            if (!fileToDelete.canWrite()) {
                return Result.failure(IllegalArgumentException("Cannot delete file: $fileName"))
            }

            // Delete the file
            val deleted = fileToDelete.delete()

            if (deleted) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete file: $fileName"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
