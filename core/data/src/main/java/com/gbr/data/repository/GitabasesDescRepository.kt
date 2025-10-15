package com.gbr.data.repository

import com.gbr.data.model.GitabasesDescResponse

interface GitabasesDescRepository {
    suspend fun getGitabasesDesc(): GitabasesDescResponse
}
