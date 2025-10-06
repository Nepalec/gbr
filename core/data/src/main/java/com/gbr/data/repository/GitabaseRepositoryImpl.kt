package com.gbr.data.repository

import com.gbr.model.gitabase.GitabaseType
import com.gbr.model.gitabase.Gitabase
import com.gbr.model.gitabase.GitabaseLang
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.prefs.Preferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitabaseRepositoryImpl @Inject constructor(
    private val localDataSource: LocalGitabaseDataSource,
    private val dataStore: DataStore<Preferences>,
    private val databaseManager: DatabaseManager
) : GitabaseRepository {

    private val _currentGitabase = MutableStateFlow<Gitabase?>(null)
    val currentGitabase: StateFlow<Gitabase?> = _currentGitabase.asStateFlow()

    private val _allGitabases = MutableStateFlow<List<Gitabase>>(emptyList())
    val allGitabases: StateFlow<List<Gitabase>> = _allGitabases.asStateFlow()

    override suspend fun getAllGitabases(): Result<List<Gitabase>> {
        return try {
            val gitabases = localDataSource.scanFolder(getDefaultPath())
            _allGitabases.value = gitabases
            Result.success(gitabases)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentGitabase(): Result<Gitabase?> {
        return try {
            val preferences = dataStore.data.first()
            val type = preferences[GitabasePreferencesKeys.CURRENT_DATABASE_TYPE] ?: "texts"
            val language = preferences[GitabasePreferencesKeys.CURRENT_DATABASE_LANGUAGE] ?: "eng"

            val gitabase = getGitabase(
                GitabaseType.valueOf(type.uppercase()),
                GitabaseLang.valueOf(language.uppercase())
            ).getOrNull()

            _currentGitabase.value = gitabase
            Result.success(gitabase)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun switchToGitabase(type: GitabaseType, gitabaseLang: GitabaseLang): Result<Unit> {
        return try {
            dataStore.edit { preferences ->
                preferences[GitabasePreferencesKeys.CURRENT_DATABASE_TYPE] = type.value
                preferences[GitabasePreferencesKeys.CURRENT_DATABASE_LANGUAGE] = gitabaseLang.value
            }

            val gitabase = getGitabase(type, gitabaseLang).getOrNull()
            _currentGitabase.value = gitabase
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun scanForGitabases(folderPath: String): Result<List<Gitabase>> {
        return try {
            val gitabases = localDataSource.scanFolder(folderPath)
            _allGitabases.value = gitabases

            // Update last scan time
            dataStore.edit { preferences ->
                preferences[GitabasePreferencesKeys.LAST_SCAN_TIME] = System.currentTimeMillis()
            }

            Result.success(gitabases)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAvailableLanguages(): Result<List<GitabaseLang>> {
        return try {
            val gitabases = _allGitabases.value.ifEmpty {
                getAllGitabases().getOrThrow()
            }
            val languages = gitabases.map { it.language }.distinct()
            Result.success(languages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAvailableTypes(): Result<List<GitabaseType>> {
        return try {
            val gitabases = _allGitabases.value.ifEmpty {
                getAllGitabases().getOrThrow()
            }
            val types = gitabases.map { it.type }.distinct()
            Result.success(types)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getDefaultPath(): String {
        val preferences = dataStore.data.first()
        return preferences[GitabasePreferencesKeys.DEFAULT_FOLDER_PATH]
            ?: getDefaultGitabasePath()
    }

    private fun getDefaultGitabasePath(): String {
        // Implementation for getting default path
        return Environment.getExternalStorageDirectory().absolutePath + "/Gitabase"
    }
}
