package com.gbr.data.repository

import com.gbr.data.model.GitabasesDescResponse
import com.gbr.model.gitabase.Gitabase

interface GitabasesDescRepository {
    suspend fun getGitabasesDesc(): GitabasesDescResponse
    suspend fun getAllGitabases(): List<Gitabase>
}
