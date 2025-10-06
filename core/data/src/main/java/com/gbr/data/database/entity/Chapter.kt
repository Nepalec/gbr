package com.gbr.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chapters")
data class Chapter(
    @PrimaryKey
    val _id: Int,
    val book_id: Double?,
    val book: String?,
    val song: String?,
    val number: Double?,
    val title: String?,
    val desc: String?,
    val colorBackgnd: String?,
    val colorForegnd: String?,
    val text_size: Int = 0,
    val purport_size: Int = 0,
    val prev_size_t: Int?,
    val prev_size_p: Int?
)
