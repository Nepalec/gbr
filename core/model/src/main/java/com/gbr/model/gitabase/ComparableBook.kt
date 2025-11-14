package com.gbr.model.gitabase

data class ComparableBook(
    val id: Int,
    val type: String,
    val compareCode: String,
    val title: String,
    val author: String,
    val issue: String,
    val abbreviation: String,
    val gitabase: Gitabase
)