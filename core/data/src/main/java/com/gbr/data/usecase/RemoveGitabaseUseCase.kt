package com.gbr.data.usecase

import android.content.Context
import com.gbr.common.strings.StringProvider
import com.gbr.data.R
import com.gbr.model.gitabase.GitabaseID
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

/**
 * Use case for removing a Gitabase file from the app's gitabase folder.
 * Deletes the physical file and updates the repository.
 */
class RemoveGitabaseUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val stringProvider: StringProvider
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
                return Result.failure(IllegalArgumentException(stringProvider.getString(R.string.error_gitabases_folder_not_exist)))
            }

            // Construct the expected filename based on gitabase ID
            val fileName = "gitabase_${gitabaseId.key}.db"
            val fileToDelete = File(gitabasesFolder, fileName)

            if (!fileToDelete.exists()) {
                return Result.failure(
                    IllegalArgumentException(
                        stringProvider.getString(
                            R.string.error_gitabase_file_not_exist,
                            fileName
                        )
                    )
                )
            }

            if (!fileToDelete.isFile) {
                return Result.failure(
                    IllegalArgumentException(
                        stringProvider.getString(
                            R.string.error_path_not_file,
                            fileName
                        )
                    )
                )
            }

            if (!fileToDelete.canWrite()) {
                return Result.failure(
                    IllegalArgumentException(
                        stringProvider.getString(
                            R.string.error_cannot_delete_file,
                            fileName
                        )
                    )
                )
            }

            // Delete the file
            val deleted = fileToDelete.delete()

            if (deleted) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(stringProvider.getString(R.string.error_failed_to_delete_file, fileName)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
