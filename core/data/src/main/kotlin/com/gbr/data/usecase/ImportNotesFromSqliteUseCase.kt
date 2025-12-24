package com.gbr.data.usecase

import com.gbr.data.repository.SqliteNotesRepositoryImpl
import com.gbr.data.repository.UserNotesRepository
import javax.inject.Inject

class ImportNotesFromSqliteUseCase @Inject constructor(
    private val userNotesRepository: UserNotesRepository,
    private val sqliteNotesRepositoryImpl: SqliteNotesRepositoryImpl
) {
    suspend fun execute(): Result<Unit> {
        // Read from local SQLite (using legacy methods)
        val notes = sqliteNotesRepositoryImpl.getNotes()
        val tags = sqliteNotesRepositoryImpl.getTags()
        val noteTags = sqliteNotesRepositoryImpl.getNoteTags()

        // Import to the current storage repository (cloud or local based on preference)
        return userNotesRepository.importFromSqlite(notes, tags, noteTags)
    }
}
