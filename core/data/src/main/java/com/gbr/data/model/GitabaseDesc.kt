package com.gbr.data.model

data class GitabaseDescNetwork(
    val gbname: String,
    val gbalias: String,
    val gblang: String,
    val lastModified: String
)

data class GitabasesDescResponse(
    val gitabases: List<GitabaseDescNetwork>,
    val success: Int,
    val message: String
)
