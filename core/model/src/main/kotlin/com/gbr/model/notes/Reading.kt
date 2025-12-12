package com.gbr.model.notes

import com.gbr.model.gitabase.GitabaseID

data class Reading(
    val gb: GitabaseID,
    val book_id:Int,
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
