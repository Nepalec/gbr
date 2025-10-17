package com.gbr.model.book

/**
 * Domain model representing a Book from a gitabase.
 * This is the clean domain model separate from database entities.
 */
data class Book(
    val id: Int,
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
    val textSize: Int?,
    val purportSize: Int?,
    val textBeginRaw: Int = 0,
    val textEndRaw: Int = 0,
    val webAbbrev: String?,
    val compareCode: String?,
    val issue: String?,
    val isSimple: Int = 0
)
