package com.gbr.data.model

import com.gbr.model.gitabase.GitabaseID

data class Gitabase(
    val id: GitabaseID,
    val name: String,
    val path: String,
    val isValid: Boolean = true,
    val lastModified: Long = System.currentTimeMillis()
)

data class BookItem(
    val id: Int,
    val title: String,
    val author: String?,
    val description: String?,
    val type: String?,
    val levels: Double?,
    val hasSanskrit: Boolean,
    val hasPurport: Boolean,
    val compareCode: String?
)
