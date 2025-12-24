package com.gbr.data.usecase

import com.gbr.data.auth.AuthRepository
import com.gbr.data.repository.UserNotesRepository
import com.gbr.data.repository.UserPreferencesRepository
import com.gbr.model.notes.NotesStorageMode
import com.gbr.network.INotesBackupImportDataSource
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ImportSqliteNotesUseCase @Inject constructor(
    private val notesBackupImportDataSource: INotesBackupImportDataSource,
    private val userNotesRepository: UserNotesRepository,
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend fun execute(fileUri: String): Result<Unit> {
        // Get current storage mode
        val storageMode = userPreferencesRepository.notesStorageMode.first()

        // Only require login for cloud storage
        if (storageMode == NotesStorageMode.CLOUD && !authRepository.isUserLoggedIn()) {
            return Result.failure(LoginRequiredException())
        }

        return try {
            // Parse the database file
            val result = notesBackupImportDataSource.importFromDatabaseFile(fileUri)

            if (result.success) {
                // Import to the current storage repository (cloud or local based on preference)
                userNotesRepository.importFromSqlite(
                    notes = result.notes,
                    tags = result.tags,
                    noteTags = result.noteTags
                )
            } else {
                Result.failure(Exception(result.error ?: "Import failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
