package com.gbr.scrDownloader.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbr.common.strings.StringProvider
import com.gbr.data.repository.GitabasesDescRepository
import com.gbr.model.gitabase.Gitabase
import com.gbr.model.gitabase.GitabaseLang
import com.gbr.scrDownloader.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class DownloaderViewModel @Inject constructor(
    private val gitabasesDescRepository: GitabasesDescRepository,
    private val savedStateHandle: SavedStateHandle,
    private val stringProvider: StringProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(DownloaderUiState())
    val uiState: StateFlow<DownloaderUiState> = _uiState.asStateFlow()

    init {
        restoreState()
        loadLanguages()
    }

    private fun restoreState() {
        val restoredState = savedStateHandle.get<DownloaderUiState>("ui_state")
        if (restoredState != null) {
            _uiState.value = restoredState
        }
    }

    private fun saveState() {
        savedStateHandle["ui_state"] = _uiState.value
    }

    private fun loadLanguages() {
        viewModelScope.launch {
            try {
                val gitabases = gitabasesDescRepository.getAllGitabases()
                val languages = gitabases.map { it.id.lang }.distinct().sortedBy { it.value }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    languages = languages,
                    gitabases = gitabases,
                    selectedLanguage = languages.firstOrNull()
                )
                saveState()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: stringProvider.getString(R.string.error_failed_to_load_languages)
                )
                saveState()
            }
        }
    }

    fun selectLanguage(language: GitabaseLang) {
        _uiState.value = _uiState.value.copy(selectedLanguage = language)
        saveState()
    }

    fun downloadGitabase(gitabase: Gitabase) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    downloadingGitabase = gitabase
                )
                saveState()

                // TODO: Implement actual download logic
                // For now, just simulate download
                kotlinx.coroutines.delay(2000)

                _uiState.value = _uiState.value.copy(
                    downloadingGitabase = null,
                    downloadedGitabases = _uiState.value.downloadedGitabases + gitabase
                )
                saveState()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    downloadingGitabase = null,
                    error = stringProvider.getString(R.string.error_failed_to_download, gitabase.title, e.message ?: "")
                )
                saveState()
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
        saveState()
    }
}

@Parcelize
data class DownloaderUiState(
    val isLoading: Boolean = true,
    val languages: List<GitabaseLang> = emptyList(),
    val gitabases: List<Gitabase> = emptyList(),
    val selectedLanguage: GitabaseLang? = null,
    val downloadingGitabase: Gitabase? = null,
    val downloadedGitabases: Set<Gitabase> = emptySet(),
    val error: String? = null
) : Parcelable
