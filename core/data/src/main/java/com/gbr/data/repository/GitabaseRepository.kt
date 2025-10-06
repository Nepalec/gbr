package com.gbr.data.repository

import com.gbr.model.gitabase.GitabaseType
import com.gbr.model.gitabase.Gitabase
import com.gbr.model.gitabase.GitabaseLang

interface GitabaseRepository {
    suspend fun getAllGitabases(): Result<List<Gitabase>>
    suspend fun getGitabase(type: GitabaseType, gitabaseLang: GitabaseLang): Result<Gitabase?>
    suspend fun getCurrentGitabase(): Result<Gitabase?>
    suspend fun switchToGitabase(type: GitabaseType, gitabaseLang: GitabaseLang): Result<Unit>
    suspend fun scanForGitabases(folderPath: String): Result<List<Gitabase>>
    suspend fun addGitabase(gitabase: Gitabase): Result<Unit>
    suspend fun removeGitabase(gitabase: Gitabase): Result<Unit>
    suspend fun getAvailableLanguages(): Result<List<GitabaseLang>>
    suspend fun getAvailableTypes(): Result<List<GitabaseType>>
}
