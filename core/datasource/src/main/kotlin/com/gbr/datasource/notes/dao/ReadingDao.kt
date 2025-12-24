package com.gbr.datasource.notes.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gbr.datasource.notes.entity.ReadingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingDao {
    @Query("SELECT * FROM readings")
    fun observeAll(): Flow<List<ReadingEntity>>

    @Query("SELECT * FROM readings WHERE id = :id")
    suspend fun getById(id: Int): ReadingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reading: ReadingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(readings: List<ReadingEntity>)

    @Delete
    suspend fun delete(reading: ReadingEntity)

    @Query("DELETE FROM readings")
    suspend fun deleteAll()
}
