package com.gbr.tabbooks.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbr.data.usecase.CopyGitabaseUseCase
import com.gbr.data.usecase.InitializeGitabasesUseCase
import com.gbr.data.usecase.RemoveGitabaseUseCase
import com.gbr.data.usecase.ScanGitabaseFilesUseCase
import com.gbr.data.repository.GitabasesRepository
import com.gbr.model.gitabase.Gitabase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.LinkedHashSet
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor(
    private val gitabasesRepository: GitabasesRepository,
    private val copyGitabaseUseCase: CopyGitabaseUseCase,
    private val removeGitabaseUseCase: RemoveGitabaseUseCase,
    private val scanGitabaseFilesUseCase: ScanGitabaseFilesUseCase,
    private val initializeGitabasesUseCase: InitializeGitabasesUseCase
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
                    gitabases = linkedSetOf(*gitabases.toTypedArray())
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

    /**
     * Copies a Gitabase file from local storage and adds it to the repository.
     *
     * @param sourceFilePath The path to the source .db file
     */
    fun copyGitabaseFromLocal(sourceFilePath: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                message = "Copying Gitabase file..."
            )

            try {
                // Copy the file to the gitabase folder
                val copyResult = copyGitabaseUseCase.execute(sourceFilePath)

                if (copyResult.isSuccess) {
                    // File copied successfully, now scan and add the new gitabase
                    val destinationFilePath = copyResult.getOrThrow()
                    addCopiedGitabaseToRepository(destinationFilePath)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to copy file: ${copyResult.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error copying file: ${e.message}"
                )
            }
        }
    }

    /**
     * Scans the copied gitabase file and adds it to the repository.
     */
    private fun addCopiedGitabaseToRepository(filePath: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    message = "Validating copied Gitabase..."
                )

                // Get the gitabase folder path
                val gitabasesFolder = File(filePath).parent ?: return@launch

                // Scan only the specific file to create a Gitabase object
                val scanResult = scanGitabaseFilesUseCase.execute(gitabasesFolder)

                if (scanResult.isSuccess) {
                    val allGitabases = scanResult.getOrThrow()

                    // Find the newly copied gitabase by matching the file path
                    val newGitabase = allGitabases.find { it.filePath == filePath }

                    if (newGitabase != null) {
                        // Add the new gitabase to the repository (Set will prevent duplicates)
                        gitabasesRepository.addGitabase(newGitabase)

                        // Update UI state
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            message = null,
                            gitabases = linkedSetOf(*gitabasesRepository.getAllGitabases().toTypedArray())
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Copied file was not recognized as a valid Gitabase"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to validate copied file: ${scanResult.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error validating copied file: ${e.message}"
                )
            }
        }
    }

    /**
     * Removes a Gitabase file and updates the repository.
     *
     * @param gitabase The gitabase to remove
     */
    fun removeGitabase(gitabase: Gitabase) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                message = "Removing Gitabase..."
            )

            try {
                // Remove the file from storage
                val removeResult = removeGitabaseUseCase.execute(gitabase.id)

                if (removeResult.isSuccess) {
                    // Remove from repository
                    gitabasesRepository.removeGitabase(gitabase)

                    // Refresh the list
                    refreshGitabases()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to remove file: ${removeResult.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error removing Gitabase: ${e.message}"
                )
            }
        }
    }
}

data class BooksUiState(
    val isLoading: Boolean = true,
    val isInitialized: Boolean = false,
    val gitabases: LinkedHashSet<Gitabase> = linkedSetOf(),
    val selectedGitabase: Gitabase? = null,
    val message: String? = null,
    val error: String? = null
)

