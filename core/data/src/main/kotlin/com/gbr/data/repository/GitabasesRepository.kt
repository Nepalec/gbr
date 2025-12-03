package com.gbr.data.repository

import com.gbr.model.gitabase.Gitabase
import com.gbr.model.gitabase.GitabaseID
import kotlinx.coroutines.flow.Flow

interface GitabasesRepository {
    fun getAllGitabases(): Set<Gitabase>
    fun getAllGitabasesFlow(): Flow<Set<Gitabase>>
    fun getCurrentGitabase(): Gitabase?
    fun getCurrentGitabaseFlow(): Flow<Gitabase?>
    fun setCurrentGitabase(gitabaseId: GitabaseID)
    fun addGitabase(gitabase: Gitabase)
    fun removeGitabase(gitabase: Gitabase)
    fun setAllGitabases(gitabases: Set<Gitabase>)
    fun setFolderPath(folderPath: String)
    fun getFolderPath(): String?
}