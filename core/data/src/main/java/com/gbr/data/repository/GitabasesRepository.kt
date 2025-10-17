package com.gbr.data.repository

import com.gbr.model.gitabase.Gitabase

interface GitabasesRepository {
    fun getAllGitabases(): Set<Gitabase>
    fun getCurrentGitabase(): Gitabase?
    fun setCurrentGitabase(gitabase: Gitabase)
    fun addGitabase(gitabase: Gitabase)
    fun removeGitabase(gitabase: Gitabase)
    fun setAllGitabases(gitabases: Set<Gitabase>)
    fun setFolderPath(folderPath: String)
    fun getFolderPath(): String?
}
