package com.gbr.data.repository

import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.notes.NoteTag
import com.gbr.model.notes.Tag
import com.gbr.model.notes.TextNote
import com.gbr.model.notes.TextNoteWithTags
import kotlinx.coroutines.flow.Flow

interface NotesCloudRepository {
    // CRUD операции для заметок (Source of Truth - Firestore)
    suspend fun createNote(note: TextNote): Result<Unit>
    suspend fun updateNote(note: TextNote): Result<Unit>
    suspend fun deleteNote(noteId: Int): Result<Unit>
    
    // CRUD операции для тегов
    suspend fun createTag(tag: Tag): Result<Unit>
    suspend fun updateTag(tag: Tag): Result<Unit>
    suspend fun deleteTag(tagId: Int): Result<Unit>
    
    // CRUD операции для связей заметок и тегов
    suspend fun addTagToNote(noteId: Int, tagId: Int): Result<Unit>
    suspend fun removeTagFromNote(noteId: Int, tagId: Int): Result<Unit>
    
    // Real-time потоки для заметок с тегами (TextNoteWithTags)
    // Автоматически обновляются при изменении notes, tags или noteTags
    fun observeNotesWithTags(): Flow<List<TextNoteWithTags>>
    fun observeNotesWithTagsByBook(gitabaseId: GitabaseID, bookId: Int): Flow<List<TextNoteWithTags>>
    fun observeNotesWithTagsByChapter(gitabaseId: GitabaseID, bookId: Int, chapter: Int): Flow<List<TextNoteWithTags>>
    fun observeNotesWithTagsByText(gitabaseId: GitabaseID, bookId: Int, textNo: String): Flow<List<TextNoteWithTags>>
    
    // Real-time потоки для тегов
    fun observeTags(): Flow<List<Tag>>
    
    // Импорт из SQLite (одноразовая операция)
    suspend fun importFromSqlite(
        notes: List<TextNote>,
        tags: List<Tag>,
        noteTags: List<NoteTag>
    ): Result<Unit>
}

