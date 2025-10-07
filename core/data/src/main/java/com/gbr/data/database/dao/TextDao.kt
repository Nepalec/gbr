package com.gbr.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gbr.data.database.entity.Text
import kotlinx.coroutines.flow.Flow

@Dao
interface TextDao {
    @Query("SELECT * FROM texts")
    fun getAllTexts(): Flow<List<Text>>

    @Query("SELECT * FROM texts WHERE _id = :id")
    suspend fun getTextById(id: Int): Text?

    @Query("SELECT * FROM texts WHERE sanskrit LIKE :query OR transl1 LIKE :query OR transl2 LIKE :query")
    suspend fun searchTexts(query: String): List<Text>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertText(text: Text)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTexts(texts: List<Text>)

    @Update
    suspend fun updateText(text: Text)

    @Delete
    suspend fun deleteText(text: Text)

    @Query("DELETE FROM texts")
    suspend fun deleteAllTexts()
}
