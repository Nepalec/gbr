package com.gbr.data.repository

import com.gbr.model.gitabase.Gitabase
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitabasesRepositoryImpl @Inject constructor() : GitabasesRepository {

    private val _availableGitabases = MutableStateFlow<List<Gitabase>>(emptyList())
    private val _currentGitabase = MutableStateFlow<Gitabase?>(null)
    private val _folderPath = MutableStateFlow<String?>(null)

    override fun getAllGitabases(): List<Gitabase> = _availableGitabases.value

    override fun getCurrentGitabase(): Gitabase? = _currentGitabase.value

    override fun setCurrentGitabase(gitabase: Gitabase) {
        _currentGitabase.value = gitabase
    }

    override fun addGitabase(gitabase: Gitabase) {
        val currentList = _availableGitabases.value.toMutableList()
        currentList.add(gitabase)
        _availableGitabases.value = currentList
    }

    override fun removeGitabase(gitabase: Gitabase) {
        val currentList = _availableGitabases.value.toMutableList()
        currentList.removeAll { it.id == gitabase.id }
        _availableGitabases.value = currentList

        // If removed gitabase was current, clear current selection
        if (_currentGitabase.value?.id == gitabase.id) {
            _currentGitabase.value = null
        }
    }

    override fun setFolderPath(folderPath: String) {
        _folderPath.value = folderPath
    }

    override fun getFolderPath(): String? = _folderPath.value
}
