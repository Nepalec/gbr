package com.gbr.datasource.notes.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gbr.datasource.notes.entity.TextNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TextNoteDao {
    @Query("SELECT * FROM text_notes")
    fun observeAll(): Flow<List<TextNoteEntity>>

    @Query("SELECT * FROM text_notes WHERE gb = :gitabaseKey AND bookId = :bookId")
    fun observeByBook(gitabaseKey: String, bookId: Int): Flow<List<TextNoteEntity>>

    @Query("SELECT * FROM text_notes WHERE gb = :gitabaseKey AND bookId = :bookId AND chapter = :chapter")
    fun observeByChapter(gitabaseKey: String, bookId: Int, chapter: Int): Flow<List<TextNoteEntity>>

    @Query("SELECT * FROM text_notes WHERE gb = :gitabaseKey AND bookId = :bookId AND textNo = :textNo")
    fun observeByText(gitabaseKey: String, bookId: Int, textNo: String): Flow<List<TextNoteEntity>>

    @Query("SELECT * FROM text_notes WHERE id = :noteId")
    suspend fun getById(noteId: Int): TextNoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: TextNoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notes: List<TextNoteEntity>)

    @Delete
    suspend fun delete(note: TextNoteEntity)

    @Query("DELETE FROM text_notes WHERE id = :noteId")
    suspend fun deleteById(noteId: Int)

    @Query("DELETE FROM text_notes")
    suspend fun deleteAll()
}
