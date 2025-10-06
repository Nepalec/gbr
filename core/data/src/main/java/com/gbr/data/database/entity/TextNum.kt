package com.gbr.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "textnums")
data class TextNum(
    @PrimaryKey
    val _id: Int,
    val book_id: Double?,
    val song: Double?,
    val ch_no: Double?,
    val txt_no: String?,
    val preview: String?,
    val haspurport: Int = 1,
    val service1: String?,
    val service2: String?,
    val text_seq_no: Int = 0,
    val text_offset: Int?,
    val text_size: Int?,
    val text_id: String?,
    val images: Int?
)
