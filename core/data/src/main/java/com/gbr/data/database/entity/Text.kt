package com.gbr.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "texts")
data class Text(
    @PrimaryKey
    val _id: Int,
    val sanskrit: String?,
    val translit: String?,
    val translit_srch: String?,
    val transl1: String?,
    val transl2: String?,
    val comment: String?
)
