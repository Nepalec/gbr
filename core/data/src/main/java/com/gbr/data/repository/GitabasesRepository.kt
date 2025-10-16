package com.gbr.data.repository

import com.gbr.model.gitabase.Gitabase

interface GitabasesRepository {
    fun getAllGitabases(): List<Gitabase>
    fun getCurrentGitabase(): Gitabase?
    fun setCurrentGitabase(gitabase: Gitabase)
    fun addGitabase(gitabase: Gitabase)
    fun removeGitabase(gitabase: Gitabase)
    fun setFolderPath(folderPath: String)
    fun getFolderPath(): String?
}
