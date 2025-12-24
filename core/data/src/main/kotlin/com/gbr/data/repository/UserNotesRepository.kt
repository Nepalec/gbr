package com.gbr.data.repository

import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.notes.NoteTag
import com.gbr.model.notes.Reading
import com.gbr.model.notes.Tag
import com.gbr.model.notes.TextNote
import com.gbr.model.notes.TextNoteWithTags
import kotlinx.coroutines.flow.Flow

/**
 * Unified repository interface for user notes.
 * Supports both local (in-memory) and cloud (Firestore) storage modes.
 * Readings are only available in local mode (returns empty list for cloud).
 */
interface UserNotesRepository {
    // Observe methods (Flow-based)
    fun observeNotesWithTags(): Flow<List<TextNoteWithTags>>
    fun observeNotesWithTagsByBook(gitabaseId: GitabaseID, bookId: Int): Flow<List<TextNoteWithTags>>
    fun observeNotesWithTagsByChapter(gitabaseId: GitabaseID, bookId: Int, chapter: Int): Flow<List<TextNoteWithTags>>
    fun observeNotesWithTagsByText(gitabaseId: GitabaseID, bookId: Int, textNo: String): Flow<List<TextNoteWithTags>>
    fun observeTags(): Flow<List<Tag>>
    fun observeReadings(): Flow<List<Reading>> // Empty for cloud, populated for local

    // CRUD operations for notes
    suspend fun createNote(note: TextNote): Result<Unit>
    suspend fun updateNote(note: TextNote): Result<Unit>
    suspend fun deleteNote(noteId: Int): Result<Unit>

    // CRUD operations for tags
    suspend fun createTag(tag: Tag): Result<Unit>
    suspend fun updateTag(tag: Tag): Result<Unit>
    suspend fun deleteTag(tagId: Int): Result<Unit>

    // CRUD operations for note-tag relationships
    suspend fun addTagToNote(noteId: Int, tagId: Int): Result<Unit>
    suspend fun removeTagFromNote(noteId: Int, tagId: Int): Result<Unit>

    // Import from SQLite (cloud only, no-op for local)
    suspend fun importFromSqlite(
        notes: List<TextNote>,
        tags: List<Tag>,
        noteTags: List<NoteTag>
    ): Result<Unit>
}
