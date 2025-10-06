package com.gbr.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "textrefs")
data class TextRef(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val thisBook: String?,
    val thisBookWeb: String?,
    val thisSong: Int?,
    val thisChapter: Int?,
    val thisTextNo: String?,
    val thisFullTextNo: String?,
    val refbyBook: String?,
    val refbyBookWeb: String?,
    val refbySong: Int?,
    val refbyChapter: Int?,
    val refbyTextNo: String?,
    val refbyText: String?,
    val refbyLevels: Int?,
    val refbyScroll: Int?,
    val refbyChapterName: String?
)
