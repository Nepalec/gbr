package com.gbr.tabbooks.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbr.data.usecase.InitializeGitabasesUseCase
import com.gbr.data.repository.GitabasesRepository
import com.gbr.model.gitabase.Gitabase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor(
    private val gitabasesRepository: GitabasesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BooksUiState())
    val uiState: StateFlow<BooksUiState> = _uiState.asStateFlow()

    init {
        loadGitabases()
    }

    private fun loadGitabases() {
        viewModelScope.launch {
            try {
                val gitabases = gitabasesRepository.getAllGitabases()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    gitabases = gitabases
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load gitabases"
                )
            }
        }
    }

    fun refreshGitabases() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        loadGitabases()
    }

    fun selectGitabase(gitabase: Gitabase) {
        _uiState.value = _uiState.value.copy(selectedGitabase = gitabase)
        gitabasesRepository.setCurrentGitabase(gitabase)
    }

    fun getCurrentGitabase(): Gitabase? {
        return gitabasesRepository.getCurrentGitabase()
    }
}

data class BooksUiState(
    val isLoading: Boolean = true,
    val isInitialized: Boolean = false,
    val gitabases: List<Gitabase> = emptyList(),
    val selectedGitabase: Gitabase? = null,
    val message: String? = null,
    val error: String? = null
)

