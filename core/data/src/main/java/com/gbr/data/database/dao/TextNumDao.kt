package com.gbr.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gbr.data.database.entity.TextNum
import kotlinx.coroutines.flow.Flow

@Dao
interface TextNumDao {
    @Query("SELECT * FROM textnums")
    fun getAllTextNums(): Flow<List<TextNum>>

    @Query("SELECT * FROM textnums WHERE _id = :id")
    suspend fun getTextNumById(id: Int): TextNum?

    @Query("SELECT * FROM textnums WHERE book_id = :bookId")
    suspend fun getTextNumsByBookId(bookId: Double): List<TextNum>

    @Query("SELECT * FROM textnums WHERE text_id = :textId")
    suspend fun getTextNumsByTextId(textId: String): List<TextNum>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTextNum(textNum: TextNum)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTextNums(textNums: List<TextNum>)

    @Update
    suspend fun updateTextNum(textNum: TextNum)

    @Delete
    suspend fun deleteTextNum(textNum: TextNum)

    @Query("DELETE FROM textnums")
    suspend fun deleteAllTextNums()
}
