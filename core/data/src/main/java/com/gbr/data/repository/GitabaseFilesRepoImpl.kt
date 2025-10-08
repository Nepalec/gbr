package com.gbr.data.repository

import com.gbr.model.gitabase.GitabaseID
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitabaseFilesRepoImpl @Inject constructor() : GitabaseFilesRepo {

    private val _availableGitabases = MutableStateFlow<List<GitabaseID>>(emptyList())
    private val _currentGitabase = MutableStateFlow<GitabaseID?>(null)
    private val _folderPath = MutableStateFlow<String?>(null)

    override fun getAllGitabases(): List<GitabaseID> = _availableGitabases.value

    override fun getCurrentGitabase(): GitabaseID? = _currentGitabase.value

    override fun setCurrentGitabase(gitabase: GitabaseID) {
        _currentGitabase.value = gitabase
    }

    override fun addGitabase(gitabase: GitabaseID) {
        val currentList = _availableGitabases.value.toMutableList()
        currentList.add(gitabase)
        _availableGitabases.value = currentList
    }

    override fun removeGitabase(gitabaseId: GitabaseID) {
        val currentList = _availableGitabases.value.toMutableList()
        currentList.removeAll { it == gitabaseId }
        _availableGitabases.value = currentList

        // If removed gitabase was current, clear current selection
        if (_currentGitabase.value == gitabaseId) {
            _currentGitabase.value = null
        }
    }

    override fun setFolderPath(folderPath: String) {
        _folderPath.value = folderPath
    }

    override fun getFolderPath(): String? = _folderPath.value
}
