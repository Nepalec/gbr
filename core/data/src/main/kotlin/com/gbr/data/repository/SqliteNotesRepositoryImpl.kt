package com.gbr.data.repository

import androidx.room.withTransaction
import com.gbr.datasource.notes.UserNotesDatabase
import com.gbr.datasource.notes.mapper.toDomain
import com.gbr.datasource.notes.mapper.toEntity
import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.notes.NoteTag
import com.gbr.model.notes.Reading
import com.gbr.model.notes.Tag
import com.gbr.model.notes.TextNote
import com.gbr.model.notes.TextNoteWithTags
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SqliteNotesRepositoryImpl @Inject constructor(
    private val database: UserNotesDatabase
) : UserNotesRepository {

    private val textNoteDao = database.textNoteDao()
    private val tagDao = database.tagDao()
    private val noteTagDao = database.noteTagDao()
    private val readingDao = database.readingDao()

    // Legacy methods for backward compatibility (if needed)
    suspend fun getNotes(): List<TextNote> {
        return textNoteDao.observeAll().first().map { it.toDomain() }
    }

    suspend fun getReadings(): List<Reading> {
        return readingDao.observeAll().first().map { it.toDomain() }
    }

    suspend fun getTags(): List<Tag> {
        return tagDao.observeAll().first().map { it.toDomain() }
    }

    suspend fun getNoteTags(): List<NoteTag> {
        return noteTagDao.observeAll().first().map { it.toDomain() }
    }

    suspend fun setNotes(notes: List<TextNote>) {
        database.withTransaction {
            textNoteDao.deleteAll()
            textNoteDao.insertAll(notes.map { it.toEntity() })
        }
    }

    suspend fun setReadings(readings: List<Reading>) {
        database.withTransaction {
            readingDao.deleteAll()
            readingDao.insertAll(readings.map { it.toEntity() })
        }
    }

    suspend fun setTags(tags: List<Tag>) {
        database.withTransaction {
            tagDao.deleteAll()
            tagDao.insertAll(tags.map { it.toEntity() })
        }
    }

    suspend fun setNoteTags(noteTags: List<NoteTag>) {
        database.withTransaction {
            noteTagDao.deleteAll()
            noteTagDao.insertAll(noteTags.map { it.toEntity() })
        }
    }

    suspend fun clearAll() {
        database.withTransaction {
            textNoteDao.deleteAll()
            readingDao.deleteAll()
            tagDao.deleteAll()
            noteTagDao.deleteAll()
        }
    }

    // UserNotesRepository implementation

    override fun observeNotesWithTags(): Flow<List<TextNoteWithTags>> {
        return combine(
            textNoteDao.observeAll().map { entities -> entities.map { it.toDomain() } },
            tagDao.observeAll().map { entities -> entities.map { it.toDomain() } },
            noteTagDao.observeAll().map { entities -> entities.map { it.toDomain() } }
        ) { notes, tags, noteTags ->
            combineNotesWithTags(notes, tags, noteTags)
        }
    }

    override fun observeNotesWithTagsByBook(
        gitabaseId: GitabaseID,
        bookId: Int
    ): Flow<List<TextNoteWithTags>> {
        val gitabaseKey = gitabaseId.key
        return combine(
            textNoteDao.observeByBook(gitabaseKey, bookId).map { entities -> entities.map { it.toDomain() } },
            tagDao.observeAll().map { entities -> entities.map { it.toDomain() } },
            noteTagDao.observeAll().map { entities -> entities.map { it.toDomain() } }
        ) { notes, tags, noteTags ->
            combineNotesWithTags(notes, tags, noteTags)
        }
    }

    override fun observeNotesWithTagsByChapter(
        gitabaseId: GitabaseID,
        bookId: Int,
        chapter: Int
    ): Flow<List<TextNoteWithTags>> {
        val gitabaseKey = gitabaseId.key
        return combine(
            textNoteDao.observeByChapter(gitabaseKey, bookId, chapter).map { entities -> entities.map { it.toDomain() } },
            tagDao.observeAll().map { entities -> entities.map { it.toDomain() } },
            noteTagDao.observeAll().map { entities -> entities.map { it.toDomain() } }
        ) { notes, tags, noteTags ->
            combineNotesWithTags(notes, tags, noteTags)
        }
    }

    override fun observeNotesWithTagsByText(
        gitabaseId: GitabaseID,
        bookId: Int,
        textNo: String
    ): Flow<List<TextNoteWithTags>> {
        val gitabaseKey = gitabaseId.key
        return combine(
            textNoteDao.observeByText(gitabaseKey, bookId, textNo).map { entities -> entities.map { it.toDomain() } },
            tagDao.observeAll().map { entities -> entities.map { it.toDomain() } },
            noteTagDao.observeAll().map { entities -> entities.map { it.toDomain() } }
        ) { notes, tags, noteTags ->
            combineNotesWithTags(notes, tags, noteTags)
        }
    }

    override fun observeTags(): Flow<List<Tag>> {
        return tagDao.observeAll().map { entities -> entities.map { it.toDomain() } }
    }

    override fun observeReadings(): Flow<List<Reading>> {
        return readingDao.observeAll().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun createNote(note: TextNote): Result<Unit> {
        return try {
            textNoteDao.insert(note.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateNote(note: TextNote): Result<Unit> {
        return try {
            textNoteDao.insert(note.toEntity()) // REPLACE strategy handles update
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteNote(noteId: Int): Result<Unit> {
        return try {
            database.withTransaction {
                // Foreign key CASCADE will handle noteTags deletion automatically
                textNoteDao.deleteById(noteId)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createTag(tag: Tag): Result<Unit> {
        return try {
            tagDao.insert(tag.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTag(tag: Tag): Result<Unit> {
        return try {
            tagDao.insert(tag.toEntity()) // REPLACE strategy handles update
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTag(tagId: Int): Result<Unit> {
        return try {
            database.withTransaction {
                // Foreign key CASCADE will handle noteTags deletion automatically
                tagDao.deleteById(tagId)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addTagToNote(noteId: Int, tagId: Int): Result<Unit> {
        return try {
            val noteTag = NoteTag(id = 0, noteId = noteId, tagId = tagId)
            noteTagDao.insert(noteTag.toEntity()) // IGNORE strategy handles duplicates
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeTagFromNote(noteId: Int, tagId: Int): Result<Unit> {
        return try {
            noteTagDao.deleteByNoteIdAndTagId(noteId, tagId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun importFromSqlite(
        notes: List<TextNote>,
        tags: List<Tag>,
        noteTags: List<NoteTag>
    ): Result<Unit> {
        return try {
            database.withTransaction {
                textNoteDao.deleteAll()
                tagDao.deleteAll()
                noteTagDao.deleteAll()

                // Insert notes and tags first
                textNoteDao.insertAll(notes.map { it.toEntity() })
                tagDao.insertAll(tags.map { it.toEntity() })

                // Create sets of valid IDs for foreign key validation
                val validNoteIds = notes.map { it.id }.toSet()
                val validTagIds = tags.map { it.id }.toSet()

                // Filter noteTags to only include those with valid foreign key references
                val validNoteTags = noteTags.filter { noteTag ->
                    validNoteIds.contains(noteTag.noteId) && validTagIds.contains(noteTag.tagId)
                }

                // Insert only valid noteTags
                noteTagDao.insertAll(validNoteTags.map { it.toEntity() })
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Combines notes, tags, and noteTags into TextNoteWithTags.
     */
    private fun combineNotesWithTags(
        notes: List<TextNote>,
        tags: List<Tag>,
        noteTags: List<NoteTag>
    ): List<TextNoteWithTags> {
        val tagsByNoteId = noteTags
            .groupBy { it.noteId }
            .mapValues { (_, noteTagsForNote) ->
                noteTagsForNote.mapNotNull { noteTag ->
                    tags.find { it.id == noteTag.tagId }
                }
            }

        return notes.map { note ->
            TextNoteWithTags(
                note = note,
                tags = tagsByNoteId[note.id] ?: emptyList()
            )
        }
    }
}
