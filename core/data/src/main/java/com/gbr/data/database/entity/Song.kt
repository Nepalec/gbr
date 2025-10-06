package com.gbr.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey
    val _id: Int,
    val book_id: Double?,
    val song: String?,
    val sort: Double?,
    val songname: String?,
    val colorBackgnd: String?,
    val colorForegnd: String?,
    val text_size: Int?,
    val purport_size: Int?,
    val subtitle: String?
)
