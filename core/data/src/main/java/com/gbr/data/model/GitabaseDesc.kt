package com.gbr.data.model

data class GitabaseDesc(
    val gbname: String,
    val gbalias: String,
    val gblang: String,
    val lastModified: String
)

data class GitabasesDescResponse(
    val gitabases: List<GitabaseDesc>,
    val success: Int,
    val message: String
)
