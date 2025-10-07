package com.gbr.data.repository

import com.gbr.data.model.Gitabase
import com.gbr.model.gitabase.GitabaseID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitabaseFilesRepoImpl @Inject constructor() : GitabaseFilesRepo {

    private val _availableGitabases = MutableStateFlow<List<Gitabase>>(emptyList())
    private val _currentGitabase = MutableStateFlow<Gitabase?>(null)
    private val _folderPath = MutableStateFlow<String?>(null)

    override fun getAvailableGitabases(): Flow<List<Gitabase>> = _availableGitabases.asStateFlow()

    override suspend fun getCurrentGitabase(): Gitabase? = _currentGitabase.value

    override suspend fun setCurrentGitabase(gitabase: Gitabase) {
        _currentGitabase.value = gitabase
    }

    override suspend fun addGitabase(gitabase: Gitabase) {
        val currentList = _availableGitabases.value.toMutableList()
        currentList.add(gitabase)
        _availableGitabases.value = currentList
    }

    override suspend fun updateGitabase(gitabase: Gitabase) {
        val currentList = _availableGitabases.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == gitabase.id }
        if (index != -1) {
            currentList[index] = gitabase
            _availableGitabases.value = currentList
        }
    }

    override suspend fun removeGitabase(gitabaseId: GitabaseID) {
        val currentList = _availableGitabases.value.toMutableList()
        currentList.removeAll { it.id == gitabaseId }
        _availableGitabases.value = currentList

        // If removed gitabase was current, clear current selection
        if (_currentGitabase.value?.id == gitabaseId) {
            _currentGitabase.value = null
        }
    }

    override suspend fun rescanFolder(): Result<List<Gitabase>> {
        // This will be implemented by the use case
        // Repository just provides the interface
        return Result.success(emptyList())
    }

    override suspend fun setFolderPath(folderPath: String) {
        _folderPath.value = folderPath
    }

    override suspend fun getFolderPath(): String? = _folderPath.value
}
