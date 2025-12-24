package com.gbr.datasource.notes.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gbr.datasource.notes.entity.NoteTagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteTagDao {
    @Query("SELECT * FROM note_tags")
    fun observeAll(): Flow<List<NoteTagEntity>>

    @Query("SELECT * FROM note_tags WHERE noteId = :noteId")
    fun observeByNoteId(noteId: Int): Flow<List<NoteTagEntity>>

    @Query("SELECT * FROM note_tags WHERE tagId = :tagId")
    fun observeByTagId(tagId: Int): Flow<List<NoteTagEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(noteTag: NoteTagEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(noteTags: List<NoteTagEntity>)

    @Query("DELETE FROM note_tags WHERE noteId = :noteId AND tagId = :tagId")
    suspend fun deleteByNoteIdAndTagId(noteId: Int, tagId: Int)

    @Query("DELETE FROM note_tags WHERE noteId = :noteId")
    suspend fun deleteByNoteId(noteId: Int)

    @Query("DELETE FROM note_tags WHERE tagId = :tagId")
    suspend fun deleteByTagId(tagId: Int)

    @Query("DELETE FROM note_tags")
    suspend fun deleteAll()
}
