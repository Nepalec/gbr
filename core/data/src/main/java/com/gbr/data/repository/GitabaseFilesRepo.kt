package com.gbr.data.repository

import com.gbr.data.model.Gitabase
import com.gbr.model.gitabase.GitabaseID

interface GitabaseFilesRepo {
    fun getAllGitabases(): List<GitabaseID>
    fun getCurrentGitabase(): GitabaseID?
    fun setCurrentGitabase(gitabase: GitabaseID)
    fun addGitabase(gitabase: GitabaseID)
    fun removeGitabase(gitabaseId: GitabaseID)
    fun setFolderPath(folderPath: String)
    fun getFolderPath(): String?
}
