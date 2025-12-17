package com.gbr.data.usecase

import com.gbr.data.auth.AuthRepository
import com.gbr.data.repository.NotesCloudRepository
import com.gbr.network.INotesBackupImportDataSource
import javax.inject.Inject

class ImportSqliteNotesUseCase @Inject constructor(
    private val notesBackupImportDataSource: INotesBackupImportDataSource,
    private val notesCloudRepository: NotesCloudRepository,
    private val authRepository: AuthRepository
) {
    suspend fun execute(fileUri: String): Result<Unit> {
        // Check if user is logged in
        if (!authRepository.isUserLoggedIn()) {
            return Result.failure(LoginRequiredException())
        }

        return try {
            // Parse the database file
            val result = notesBackupImportDataSource.importFromDatabaseFile(fileUri)
            
            if (result.success) {
                // Import directly to cloud repository
                notesCloudRepository.importFromSqlite(
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

