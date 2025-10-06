package com.gbr.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_nums")
data class ImageNum(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bid: Int?,
    val sid: Int?,
    val cid: Int?,
    val tnum: String?,
    val text_id: String?,
    val image_id: String?,
    val type: Int?,
    val desc: String?,
    val kind: Int?
)
