package com.gbr.data.usecase

import com.gbr.data.repository.NotesCloudRepository
import com.gbr.data.repository.SqliteNotesRepository
import javax.inject.Inject

class ImportNotesFromSqliteUseCase @Inject constructor(
    private val notesCloudRepository: NotesCloudRepository,
    private val sqliteNotesRepository: SqliteNotesRepository
) {
    suspend fun execute(): Result<Unit> {
        val notes = sqliteNotesRepository.getNotes()
        val tags = sqliteNotesRepository.getTags()
        val noteTags = sqliteNotesRepository.getNoteTags()
        
        return notesCloudRepository.importFromSqlite(notes, tags, noteTags)
    }
}

