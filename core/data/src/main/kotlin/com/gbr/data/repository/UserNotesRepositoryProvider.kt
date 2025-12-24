package com.gbr.data.repository

import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.notes.NoteTag
import com.gbr.model.notes.NotesStorageMode
import com.gbr.model.notes.Reading
import com.gbr.model.notes.Tag
import com.gbr.model.notes.TextNote
import com.gbr.model.notes.TextNoteWithTags
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wrapper that provides the correct UserNotesRepository implementation
 * based on the current storage mode preference.
 */
@Singleton
class UserNotesRepositoryProvider @Inject constructor(
    private val sqliteNotesRepository: SqliteNotesRepositoryImpl,
    private val notesCloudRepository: NotesCloudRepositoryImpl,
    private val userPreferencesRepository: UserPreferencesRepository
) : UserNotesRepository {

    /**
     * Gets the current repository based on storage mode.
     */
    private suspend fun getCurrentRepository(): UserNotesRepository {
        val storageMode = userPreferencesRepository.notesStorageMode.first()
        return when (storageMode) {
            NotesStorageMode.LOCAL -> sqliteNotesRepository
            NotesStorageMode.CLOUD -> notesCloudRepository
        }
    }

    override fun observeNotesWithTags(): Flow<List<TextNoteWithTags>> {
        return userPreferencesRepository.notesStorageMode.flatMapLatest { mode ->
            when (mode) {
                NotesStorageMode.LOCAL -> sqliteNotesRepository.observeNotesWithTags()
                NotesStorageMode.CLOUD -> notesCloudRepository.observeNotesWithTags()
            }
        }
    }

    override fun observeNotesWithTagsByBook(
        gitabaseId: GitabaseID,
        bookId: Int
    ): Flow<List<TextNoteWithTags>> {
        return userPreferencesRepository.notesStorageMode.flatMapLatest { mode ->
            when (mode) {
                NotesStorageMode.LOCAL -> sqliteNotesRepository.observeNotesWithTagsByBook(gitabaseId, bookId)
                NotesStorageMode.CLOUD -> notesCloudRepository.observeNotesWithTagsByBook(gitabaseId, bookId)
            }
        }
    }

    override fun observeNotesWithTagsByChapter(
        gitabaseId: GitabaseID,
        bookId: Int,
        chapter: Int
    ): Flow<List<TextNoteWithTags>> {
        return userPreferencesRepository.notesStorageMode.flatMapLatest { mode ->
            when (mode) {
                NotesStorageMode.LOCAL -> sqliteNotesRepository.observeNotesWithTagsByChapter(gitabaseId, bookId, chapter)
                NotesStorageMode.CLOUD -> notesCloudRepository.observeNotesWithTagsByChapter(gitabaseId, bookId, chapter)
            }
        }
    }

    override fun observeNotesWithTagsByText(
        gitabaseId: GitabaseID,
        bookId: Int,
        textNo: String
    ): Flow<List<TextNoteWithTags>> {
        return userPreferencesRepository.notesStorageMode.flatMapLatest { mode ->
            when (mode) {
                NotesStorageMode.LOCAL -> sqliteNotesRepository.observeNotesWithTagsByText(gitabaseId, bookId, textNo)
                NotesStorageMode.CLOUD -> notesCloudRepository.observeNotesWithTagsByText(gitabaseId, bookId, textNo)
            }
        }
    }

    override fun observeTags(): Flow<List<Tag>> {
        return userPreferencesRepository.notesStorageMode.flatMapLatest { mode ->
            when (mode) {
                NotesStorageMode.LOCAL -> sqliteNotesRepository.observeTags()
                NotesStorageMode.CLOUD -> notesCloudRepository.observeTags()
            }
        }
    }

    override fun observeReadings(): Flow<List<Reading>> {
        return userPreferencesRepository.notesStorageMode.flatMapLatest { mode ->
            when (mode) {
                NotesStorageMode.LOCAL -> sqliteNotesRepository.observeReadings()
                NotesStorageMode.CLOUD -> notesCloudRepository.observeReadings()
            }
        }
    }

    override suspend fun createNote(note: TextNote): Result<Unit> {
        return getCurrentRepository().createNote(note)
    }

    override suspend fun updateNote(note: TextNote): Result<Unit> {
        return getCurrentRepository().updateNote(note)
    }

    override suspend fun deleteNote(noteId: Int): Result<Unit> {
        return getCurrentRepository().deleteNote(noteId)
    }

    override suspend fun createTag(tag: Tag): Result<Unit> {
        return getCurrentRepository().createTag(tag)
    }

    override suspend fun updateTag(tag: Tag): Result<Unit> {
        return getCurrentRepository().updateTag(tag)
    }

    override suspend fun deleteTag(tagId: Int): Result<Unit> {
        return getCurrentRepository().deleteTag(tagId)
    }

    override suspend fun addTagToNote(noteId: Int, tagId: Int): Result<Unit> {
        return getCurrentRepository().addTagToNote(noteId, tagId)
    }

    override suspend fun removeTagFromNote(noteId: Int, tagId: Int): Result<Unit> {
        return getCurrentRepository().removeTagFromNote(noteId, tagId)
    }

    override suspend fun importFromSqlite(
        notes: List<TextNote>,
        tags: List<Tag>,
        noteTags: List<NoteTag>
    ): Result<Unit> {
        return getCurrentRepository().importFromSqlite(notes, tags, noteTags)
    }
}
