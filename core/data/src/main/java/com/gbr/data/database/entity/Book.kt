package com.gbr.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey
    val _id: Int,
    val sort: Int?,
    val author: String?,
    val title: String?,
    val desc: String?,
    val type: String?,
    val levels: Double?,
    val hasSanskrit: Int = 0,
    val hasPurport: Int = 1,
    val hasColorStructure: Int?,
    val isSongBook: Int = 0,
    val text_size: Int?,
    val purport_size: Int?,
    val text_begin_raw: Int = 0,
    val text_end_raw: Int = 0,
    val web_abbrev: String?,
    val compare_code: String?,
    val issue: String?,
    val isSimple: Int = 0
)
