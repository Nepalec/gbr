package com.gbr.network

import com.gbr.model.notes.NoteTag
import com.gbr.model.notes.Reading
import com.gbr.model.notes.Tag
import com.gbr.model.notes.TextNote

interface INotesBackupImportDataSource {
    suspend fun importFromDatabaseFile(fileUri: String): NotesBackupImportResult
}

data class NotesBackupImportResult(
    val notes: List<TextNote>,
    val readings: List<Reading>,
    val tags: List<Tag>,
    val noteTags: List<NoteTag>,
    val success: Boolean,
    val error: String? = null
)
