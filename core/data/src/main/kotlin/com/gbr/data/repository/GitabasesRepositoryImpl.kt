package com.gbr.data.repository

import com.gbr.model.gitabase.Gitabase
import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.gitabase.GitabaseType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitabasesRepositoryImpl @Inject constructor() : GitabasesRepository {

    private val _availableGitabases = MutableStateFlow<LinkedHashSet<Gitabase>>(linkedSetOf())
    private val _currentGitabase = MutableStateFlow<Gitabase?>(null)
    private val _folderPath = MutableStateFlow<String?>(null)

    override fun getAllGitabases(): Set<Gitabase> {
        val gitabases = _availableGitabases.value
        return gitabases.sortedWith(compareBy<Gitabase> { it.id.type == GitabaseType.HELP }.thenBy { it.title }).toSet()
    }

    override fun getAllGitabasesFlow(): Flow<Set<Gitabase>> {
        return _availableGitabases.map { gitabases ->
            gitabases.sortedWith(compareBy<Gitabase> { it.id.type == GitabaseType.HELP }.thenBy { it.title }).toSet()
        }
    }

    override fun getCurrentGitabase(): Gitabase? = _currentGitabase.value

    override fun getCurrentGitabaseFlow() = _currentGitabase

    override fun setCurrentGitabase(gitabaseId: GitabaseID) {
        val gitabase = _availableGitabases.value.find { it.id == gitabaseId }
        _currentGitabase.value = gitabase
    }

    override fun addGitabase(gitabase: Gitabase) {
        val currentSet = _availableGitabases.value
        currentSet.add(gitabase)
        _availableGitabases.value = currentSet
    }

    override fun removeGitabase(gitabase: Gitabase) {
        val currentSet = _availableGitabases.value
        currentSet.remove(gitabase)
        _availableGitabases.value = currentSet

        // If removed gitabase was current, clear current selection
        if (_currentGitabase.value?.id == gitabase.id) {
            _currentGitabase.value = null
        }
    }

    override fun setAllGitabases(gitabases: Set<Gitabase>) {
        _availableGitabases.value = LinkedHashSet(gitabases)
    }

    override fun setFolderPath(folderPath: String) {
        _folderPath.value = folderPath
    }

    override fun getFolderPath(): String? = _folderPath.value
}