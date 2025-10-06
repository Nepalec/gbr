package com.gbr.data.database.dao

import androidx.room.*
import com.gbr.data.database.entity.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM songs")
    fun getAllSongs(): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE _id = :id")
    suspend fun getSongById(id: Int): Song?

    @Query("SELECT * FROM songs WHERE book_id = :bookId")
    suspend fun getSongsByBookId(bookId: Double): List<Song>

    @Query("SELECT * FROM songs WHERE song = :songName")
    suspend fun getSongsBySongName(songName: String): List<Song>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: Song)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<Song>)

    @Update
    suspend fun updateSong(song: Song)

    @Delete
    suspend fun deleteSong(song: Song)

    @Query("DELETE FROM songs")
    suspend fun deleteAllSongs()
}
