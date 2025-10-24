package com.gbr.tabbooks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbr.data.usecase.CopyGitabaseUseCase
import com.gbr.data.usecase.RemoveGitabaseUseCase
import com.gbr.data.usecase.ScanGitabaseFilesUseCase
import com.gbr.data.usecase.SetCurrentGitabaseUseCase
import com.gbr.data.repository.GitabasesRepository
import com.gbr.data.repository.TextsRepository
import com.gbr.model.gitabase.Gitabase
import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.gitabase.GitabaseType
import com.gbr.model.gitabase.GitabaseLang
import java.io.File
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.LinkedHashSet
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor(
    private val gitabasesRepository: GitabasesRepository,
    private val textsRepository: TextsRepository,
    private val copyGitabaseUseCase: CopyGitabaseUseCase,
    private val removeGitabaseUseCase: RemoveGitabaseUseCase,
    private val scanGitabaseFilesUseCase: ScanGitabaseFilesUseCase,
    private val setCurrentGitabaseUseCase: SetCurrentGitabaseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BooksUiState())
    val uiState: StateFlow<BooksUiState> = _uiState.asStateFlow()

    init {
        loadGitabases()
        observeCurrentGitabase()
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

    private fun observeCurrentGitabase() {
        viewModelScope.launch {
            gitabasesRepository.getCurrentGitabaseFlow().collect { currentGitabase ->
                _uiState.value = _uiState.value.copy(selectedGitabase = currentGitabase)

                // Load books when gitabase changes
                if (currentGitabase != null) {
                    loadBooks(currentGitabase.id)
                } else {
                    _uiState.value = _uiState.value.copy(books = emptyList())
                }
            }
        }
    }

    private fun loadBooks(gitabaseId: GitabaseID) {
        viewModelScope.launch {
            try {
                val result = textsRepository.getAllBooks(gitabaseId)
                result.fold(
                    onSuccess = { books ->
                        _uiState.value = _uiState.value.copy(books = books)
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            books = emptyList(),
                            error = "Failed to load books: ${error.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    books = emptyList(),
                    error = "Error loading books: ${e.message}"
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

        viewModelScope.launch {
            setCurrentGitabaseUseCase.execute(gitabase.id)
        }
    }

    /**
     * Copies a Gitabase file from local storage and adds it to the repository.
     *
     * @param sourceFilePath The path to the source .db file
     */
    fun copyGitabaseFromLocal(sourceFilePath: String) {
        viewModelScope.launch {
            // Create a temporary gitabase for immediate display with shimmering
            val tempGitabase = createTempGitabaseFromPath(sourceFilePath)

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                message = "Copying Gitabase file...",
                copyingGitabases = setOf(tempGitabase)
            )

            try {
                // Copy the file to the gitabase folder
                val copyResult = copyGitabaseUseCase.execute(sourceFilePath)

                if (copyResult.isSuccess) {
                    // File copied successfully, now scan and add the new gitabase
                    val destinationFilePath = copyResult.getOrThrow()
                    addCopiedGitabaseToRepository(destinationFilePath, tempGitabase)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to copy file: ${copyResult.exceptionOrNull()?.message}",
                        copyingGitabases = emptySet()
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error copying file: ${e.message}",
                    copyingGitabases = emptySet()
                )
            }
        }
    }

    /**
     * Creates a temporary gitabase from file path for immediate display.
     */
    private fun createTempGitabaseFromPath(filePath: String): Gitabase {
        val fileName = File(filePath).nameWithoutExtension
        val parts = fileName.split("_")
        val type = if (parts.size >= 2) parts[1] else "unknown"
        val lang = if (parts.size >= 3) parts[2] else "unknown"

        return Gitabase(
            id = GitabaseID(
                type = GitabaseType(type),
                lang = GitabaseLang(lang)
            ),
            title = fileName,
            version = 1,
            filePath = filePath,
            lastModified = "Copying..."
        )
    }

    /**
     * Scans the copied gitabase file and adds it to the repository.
     */
    private fun addCopiedGitabaseToRepository(filePath: String, tempGitabase: Gitabase) {
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

                        // Set the new gitabase as current
                        setCurrentGitabaseUseCase.execute(newGitabase.id)

                        // Update UI state - remove from copying set and set as selected
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            message = null,
                            gitabases = linkedSetOf(*gitabasesRepository.getAllGitabases().toTypedArray()),
                            copyingGitabases = emptySet(),
                            selectedGitabase = newGitabase
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Copied file was not recognized as a valid Gitabase",
                            copyingGitabases = emptySet()
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to validate copied file: ${scanResult.exceptionOrNull()?.message}",
                        copyingGitabases = emptySet()
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error validating copied file: ${e.message}",
                    copyingGitabases = emptySet()
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
    val books: List<com.gbr.model.book.BookPreview> = emptyList(),
    val message: String? = null,
    val error: String? = null,
    val copyingGitabases: Set<Gitabase> = emptySet()
)

