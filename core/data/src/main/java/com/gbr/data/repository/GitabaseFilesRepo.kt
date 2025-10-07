package com.gbr.data.repository

import com.gbr.data.model.Gitabase
import com.gbr.model.gitabase.GitabaseID
import kotlinx.coroutines.flow.Flow

interface GitabaseFilesRepo {
    fun getAvailableGitabases(): Flow<List<Gitabase>>
    suspend fun getCurrentGitabase(): Gitabase?
    suspend fun setCurrentGitabase(gitabase: Gitabase)
    suspend fun addGitabase(gitabase: Gitabase)
    suspend fun updateGitabase(gitabase: Gitabase)
    suspend fun removeGitabase(gitabaseId: GitabaseID)
    suspend fun rescanFolder(): Result<List<Gitabase>>
    suspend fun setFolderPath(folderPath: String)
    suspend fun getFolderPath(): String?
}
