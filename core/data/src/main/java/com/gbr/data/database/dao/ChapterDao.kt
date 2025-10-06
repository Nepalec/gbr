package com.gbr.data.database.dao

import androidx.room.*
import com.gbr.data.database.entity.Chapter
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {
    @Query("SELECT * FROM chapters")
    fun getAllChapters(): Flow<List<Chapter>>

    @Query("SELECT * FROM chapters WHERE _id = :id")
    suspend fun getChapterById(id: Int): Chapter?

    @Query("SELECT * FROM chapters WHERE book_id = :bookId")
    suspend fun getChaptersByBookId(bookId: Double): List<Chapter>

    @Query("SELECT * FROM chapters WHERE book = :bookName")
    suspend fun getChaptersByBookName(bookName: String): List<Chapter>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: Chapter)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<Chapter>)

    @Update
    suspend fun updateChapter(chapter: Chapter)

    @Delete
    suspend fun deleteChapter(chapter: Chapter)

    @Query("DELETE FROM chapters")
    suspend fun deleteAllChapters()
}
