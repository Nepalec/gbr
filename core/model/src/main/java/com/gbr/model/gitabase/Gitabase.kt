package com.gbr.model.gitabase

data class Gitabase(
    val id: GitabaseID,
    val title: String,
    val version: Int,
    val filePath: String,
    val isShopDatabase: Boolean = false,
    val hasTranslation: Boolean = false,
    val lastModified: String
)
