package com.gbr.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gbr.data.database.entity.Image
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Query("SELECT * FROM images")
    fun getAllImages(): Flow<List<Image>>

    @Query("SELECT * FROM images WHERE image_id = :imageId")
    suspend fun getImageById(imageId: String): Image?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: Image)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<Image>)

    @Update
    suspend fun updateImage(image: Image)

    @Delete
    suspend fun deleteImage(image: Image)

    @Query("DELETE FROM images")
    suspend fun deleteAllImages()
}
