package com.gbr.network

import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.notes.NoteTag
import com.gbr.model.notes.Tag
import com.gbr.model.notes.TextNote
import kotlinx.coroutines.flow.Flow

interface INotesCloudDataSource {
    // CRUD операции для заметок
    suspend fun saveNote(userId: String, note: TextNote): Result<Unit>
    suspend fun updateNote(userId: String, note: TextNote): Result<Unit>
    suspend fun deleteNote(userId: String, noteId: Int): Result<Unit>
    
    // Real-time listeners для заметок (Firestore snapshot listeners)
    fun observeNotes(userId: String): Flow<List<TextNote>>
    fun observeNotesByBook(userId: String, gitabaseId: GitabaseID, bookId: Int): Flow<List<TextNote>>
    fun observeNotesByChapter(userId: String, gitabaseId: GitabaseID, bookId: Int, chapter: Int): Flow<List<TextNote>>
    fun observeNotesByText(userId: String, gitabaseId: GitabaseID, bookId: Int, textNo: String): Flow<List<TextNote>>
    
    // CRUD операции для тегов
    suspend fun saveTag(userId: String, tag: Tag): Result<Unit>
    suspend fun updateTag(userId: String, tag: Tag): Result<Unit>
    suspend fun deleteTag(userId: String, tagId: Int): Result<Unit>
    fun observeTags(userId: String): Flow<List<Tag>>
    
    // CRUD операции для связей заметок и тегов
    suspend fun saveNoteTag(userId: String, noteTag: NoteTag): Result<Unit>
    suspend fun deleteNoteTag(userId: String, noteTagId: Int): Result<Unit>
    fun observeNoteTags(userId: String): Flow<List<NoteTag>>
    
    // Импорт из SQLite (одноразовая операция, batch write)
    suspend fun importNotesFromSqlite(userId: String, notes: List<TextNote>): Result<Unit>
    suspend fun importTagsFromSqlite(userId: String, tags: List<Tag>): Result<Unit>
    suspend fun importNoteTagsFromSqlite(userId: String, noteTags: List<NoteTag>): Result<Unit>
}

