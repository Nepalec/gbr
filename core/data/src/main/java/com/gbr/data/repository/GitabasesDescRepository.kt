package com.gbr.data.repository

import com.gbr.data.model.GitabasesDescResponse
import com.gbr.model.gitabase.Gitabase

interface GitabasesDescRepository {
    suspend fun getGitabasesDesc(is4Download: Boolean): GitabasesDescResponse
    suspend fun getAllGitabases(): List<Gitabase>
    suspend fun getDownloadableGitabases(): List<Gitabase>
}