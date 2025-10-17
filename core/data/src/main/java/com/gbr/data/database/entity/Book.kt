package com.gbr.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = false)
    val _id: Int?,
    val sort: Int?,
    val author: String?,
    val title: String?,
    val desc: String?,
    val type: String?,
    val levels: Double?,
    val hasSanskrit: Int?,
    val hasPurport: Int?,
    val hasColorStructure: Int?,
    val isSongBook: Int?,
    val text_size: Int?,
    val purport_size: Int?,
    val text_begin_raw: Int?,
    val text_end_raw: Int?,
    val web_abbrev: String?,
    val compare_code: String?,
    val issue: String?,
    val isSimple: Int?
)
