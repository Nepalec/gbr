package com.gbr.datasource.notes.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.gbr.datasource.notes.converters.NotesTypeConverters

@Entity(tableName = "readings")
@TypeConverters(NotesTypeConverters::class)
data class ReadingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val gb: String, // GitabaseID stored as String (key)
    val book_id: Int,
    val volumeNo: Int?,
    val chapterNo: Int?,
    val textNo: String,
    val levels: Int,
    val textId: String,
    val author: String,
    val title: String,
    val subtitle: String?,
    val textCode: String,
    val scroll: Int,
    val progress: Int,
    val created: Long,
    val modified: Long,
    val scratch: Int,
    val readingTime: Long
)
