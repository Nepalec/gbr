package com.gbr.data.usecase

import com.gbr.data.repository.SqliteNotesRepository
import com.gbr.network.INotesBackupImportDataSource
import javax.inject.Inject

class ImportSqliteNotesUseCase @Inject constructor(
    private val notesBackupImportDataSource: INotesBackupImportDataSource,
    private val sqliteNotesRepository: SqliteNotesRepository
) {
    suspend fun execute(fileUri: String): Result<Unit> {
        return try {
            val result = notesBackupImportDataSource.importFromDatabaseFile(fileUri)
            
            if (result.success) {
                sqliteNotesRepository.setNotes(result.notes)
                sqliteNotesRepository.setReadings(result.readings)
                sqliteNotesRepository.setTags(result.tags)
                sqliteNotesRepository.setNoteTags(result.noteTags)
                Result.success(Unit)
            } else {
                Result.failure(Exception(result.error ?: "Import failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

