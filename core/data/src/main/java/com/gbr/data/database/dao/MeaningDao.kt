package com.gbr.data.database.dao

import androidx.room.*
import com.gbr.data.database.entity.Meaning
import kotlinx.coroutines.flow.Flow

@Dao
interface MeaningDao {
    @Query("SELECT * FROM meanings")
    fun getAllMeanings(): Flow<List<Meaning>>

    @Query("SELECT * FROM meanings WHERE _id = :id")
    suspend fun getMeaningById(id: Int): Meaning?

    @Query("SELECT * FROM meanings WHERE word LIKE :query")
    suspend fun searchMeanings(query: String): List<Meaning>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeaning(meaning: Meaning)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeanings(meanings: List<Meaning>)

    @Update
    suspend fun updateMeaning(meaning: Meaning)

    @Delete
    suspend fun deleteMeaning(meaning: Meaning)

    @Query("DELETE FROM meanings")
    suspend fun deleteAllMeanings()
}
