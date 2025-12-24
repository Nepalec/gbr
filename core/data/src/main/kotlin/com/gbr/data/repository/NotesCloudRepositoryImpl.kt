package com.gbr.data.repository

import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.notes.NoteTag
import com.gbr.model.notes.Reading
import com.gbr.model.notes.Tag
import com.gbr.model.notes.TextNote
import com.gbr.model.notes.TextNoteWithTags
import com.gbr.network.IAuthStatusDataSource
import com.gbr.network.INotesCloudDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesCloudRepositoryImpl @Inject constructor(
    private val notesCloudDataSource: INotesCloudDataSource,
    private val authStatusDataSource: IAuthStatusDataSource
) : UserNotesRepository {

    override suspend fun createNote(note: TextNote): Result<Unit> {
        val userId = authStatusDataSource.getCurrentUserId()
            ?: return Result.failure(IllegalStateException("User not logged in"))
        return notesCloudDataSource.saveNote(userId, note)
    }

    override suspend fun updateNote(note: TextNote): Result<Unit> {
        val userId = authStatusDataSource.getCurrentUserId()
            ?: return Result.failure(IllegalStateException("User not logged in"))
        return notesCloudDataSource.updateNote(userId, note)
    }

    override suspend fun deleteNote(noteId: Int): Result<Unit> {
        val userId = authStatusDataSource.getCurrentUserId()
            ?: return Result.failure(IllegalStateException("User not logged in"))
        return notesCloudDataSource.deleteNote(userId, noteId)
    }

    override suspend fun createTag(tag: Tag): Result<Unit> {
        val userId = authStatusDataSource.getCurrentUserId()
            ?: return Result.failure(IllegalStateException("User not logged in"))
        return notesCloudDataSource.saveTag(userId, tag)
    }

    override suspend fun updateTag(tag: Tag): Result<Unit> {
        val userId = authStatusDataSource.getCurrentUserId()
            ?: return Result.failure(IllegalStateException("User not logged in"))
        return notesCloudDataSource.updateTag(userId, tag)
    }

    override suspend fun deleteTag(tagId: Int): Result<Unit> {
        val userId = authStatusDataSource.getCurrentUserId()
            ?: return Result.failure(IllegalStateException("User not logged in"))
        return notesCloudDataSource.deleteTag(userId, tagId)
    }

    override suspend fun addTagToNote(noteId: Int, tagId: Int): Result<Unit> {
        val userId = authStatusDataSource.getCurrentUserId()
            ?: return Result.failure(IllegalStateException("User not logged in"))
        val noteTag = NoteTag(id = 0, noteId = noteId, tagId = tagId)
        return notesCloudDataSource.saveNoteTag(userId, noteTag)
    }

    override suspend fun removeTagFromNote(noteId: Int, tagId: Int): Result<Unit> {
        val userId = authStatusDataSource.getCurrentUserId()
            ?: return Result.failure(IllegalStateException("User not logged in"))
        // Find noteTag by noteId and tagId, then delete
        // For now, we'll need to query noteTags to find the matching one
        // This is a simplified implementation - in production, consider storing noteTagId in the document
        return Result.failure(NotImplementedError("Need to implement noteTag lookup by noteId and tagId"))
    }

    override fun observeNotesWithTags(): Flow<List<TextNoteWithTags>> {
        return authStatusDataSource.observeAuthState()
            .flatMapLatest { authState ->
                val userId = authState.userId
                if (authState.isLoggedIn && userId != null) {
                    combineNotesWithTags(
                        notesCloudDataSource.observeNotes(userId),
                        notesCloudDataSource.observeTags(userId),
                        notesCloudDataSource.observeNoteTags(userId)
                    )
                } else {
                    flowOf(emptyList())
                }
            }
    }

    override fun observeNotesWithTagsByBook(
        gitabaseId: GitabaseID,
        bookId: Int
    ): Flow<List<TextNoteWithTags>> {
        return authStatusDataSource.observeAuthState()
            .flatMapLatest { authState ->
                val userId = authState.userId
                if (authState.isLoggedIn && userId != null) {
                    combineNotesWithTags(
                        notesCloudDataSource.observeNotesByBook(
                            userId,
                            gitabaseId,
                            bookId
                        ),
                        notesCloudDataSource.observeTags(userId),
                        notesCloudDataSource.observeNoteTags(userId)
                    )
                } else {
                    flowOf(emptyList())
                }
            }
    }

    override fun observeNotesWithTagsByChapter(
        gitabaseId: GitabaseID,
        bookId: Int,
        chapter: Int
    ): Flow<List<TextNoteWithTags>> {
        return authStatusDataSource.observeAuthState()
            .flatMapLatest { authState ->
                val userId = authState.userId
                if (authState.isLoggedIn && userId != null) {
                    combineNotesWithTags(
                        notesCloudDataSource.observeNotesByChapter(
                            userId,
                            gitabaseId,
                            bookId,
                            chapter
                        ),
                        notesCloudDataSource.observeTags(userId),
                        notesCloudDataSource.observeNoteTags(userId)
                    )
                } else {
                    flowOf(emptyList())
                }
            }
    }

    override fun observeNotesWithTagsByText(
        gitabaseId: GitabaseID,
        bookId: Int,
        textNo: String
    ): Flow<List<TextNoteWithTags>> {
        return authStatusDataSource.observeAuthState()
            .flatMapLatest { authState ->
                val userId = authState.userId
                if (authState.isLoggedIn && userId != null) {
                    combineNotesWithTags(
                        notesCloudDataSource.observeNotesByText(
                            userId,
                            gitabaseId,
                            bookId,
                            textNo
                        ),
                        notesCloudDataSource.observeTags(userId),
                        notesCloudDataSource.observeNoteTags(userId)
                    )
                } else {
                    flowOf(emptyList())
                }
            }
    }

    override fun observeTags(): Flow<List<Tag>> {
        return authStatusDataSource.observeAuthState()
            .flatMapLatest { authState ->
                val userId = authState.userId
                if (authState.isLoggedIn && userId != null) {
                    notesCloudDataSource.observeTags(userId)
                } else {
                    flowOf(emptyList())
                }
            }
    }

    override fun observeReadings(): Flow<List<Reading>> {
        // Cloud storage doesn't support readings, return empty list
        return flowOf(emptyList())
    }

    override suspend fun importFromSqlite(
        notes: List<TextNote>,
        tags: List<Tag>,
        noteTags: List<NoteTag>
    ): Result<Unit> {
        val userId = authStatusDataSource.getCurrentUserId()
            ?: return Result.failure(IllegalStateException("User not logged in"))

        return runCatching {
            notesCloudDataSource.importNotesFromSqlite(userId, notes).getOrThrow()
            notesCloudDataSource.importTagsFromSqlite(userId, tags).getOrThrow()
            notesCloudDataSource.importNoteTagsFromSqlite(userId, noteTags).getOrThrow()
        }
    }

    /**
     * Комбинирует потоки notes, tags и noteTags в TextNoteWithTags.
     * Автоматически обновляется при изменении любого из компонентов.
     */
    private fun combineNotesWithTags(
        notesFlow: Flow<List<TextNote>>,
        tagsFlow: Flow<List<Tag>>,
        noteTagsFlow: Flow<List<NoteTag>>
    ): Flow<List<TextNoteWithTags>> {
        return combine(notesFlow, tagsFlow, noteTagsFlow) { notes, tags, noteTags ->
            // Создаем map для быстрого поиска тегов по noteId
            val tagsByNoteId = noteTags
                .groupBy { it.noteId }
                .mapValues { (_, noteTagsForNote) ->
                    noteTagsForNote.mapNotNull { noteTag ->
                        tags.find { it.id == noteTag.tagId }
                    }
                }

            // Объединяем заметки с их тегами
            notes.map { note ->
                TextNoteWithTags(
                    note = note,
                    tags = tagsByNoteId[note.id] ?: emptyList()
                )
            }
        }
    }
}
