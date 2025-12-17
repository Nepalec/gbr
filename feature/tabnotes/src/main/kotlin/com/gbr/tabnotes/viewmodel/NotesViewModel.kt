package com.gbr.tabnotes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbr.data.auth.AuthRepository
import com.gbr.data.repository.NotesCloudRepository
import com.gbr.data.repository.SqliteNotesRepository
import com.gbr.data.usecase.ImportSqliteNotesUseCase
import com.gbr.data.usecase.LoginRequiredException
import com.gbr.model.notes.NoteTag
import com.gbr.model.notes.Reading
import com.gbr.model.notes.Tag
import com.gbr.model.notes.TextNote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val importSqliteNotesUseCase: ImportSqliteNotesUseCase,
    private val sqliteNotesRepository: SqliteNotesRepository,
    private val authRepository: AuthRepository,
    private val notesCloudRepository: NotesCloudRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    private var onNavigateToLogin: (() -> Unit)? = null

    fun setNavigateToLoginCallback(callback: () -> Unit) {
        onNavigateToLogin = callback
    }

    init {
        observeNotesFromCloud()
        observeAuthState()
    }

    private fun observeNotesFromCloud() {
        // Combine notes with tags and tags flows to get all data
        combine(
            notesCloudRepository.observeNotesWithTags(),
            notesCloudRepository.observeTags()
        ) { notesWithTags, allTags ->
            // Extract notes from TextNoteWithTags
            val notes = notesWithTags.map { it.note }

            // Extract tags (use allTags from observeTags for complete list)
            val tags = allTags

            // Reconstruct noteTags from TextNoteWithTags
            val noteTags = notesWithTags.flatMap { noteWithTags ->
                noteWithTags.tags.map { tag ->
                    NoteTag(
                        id=0,
                        noteId = noteWithTags.note.id,
                        tagId = tag.id
                    )
                }
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                notes = notes,
                readings = emptyList(), // Readings are not stored in cloud, only in local SQLite
                tags = tags,
                noteTags = noteTags
            )
        }.launchIn(viewModelScope)
    }

    fun refreshNotes() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        // Data will automatically update via the flow observers
    }

    private fun observeAuthState() {
        authRepository.observeAuthState()
            .onEach { authData ->
                // If user just logged in and there's a pending import, retry it
                if (authData.isLoggedIn && _uiState.value.pendingImportFileUri != null) {
                    val pendingUri = _uiState.value.pendingImportFileUri
                    _uiState.value = _uiState.value.copy(pendingImportFileUri = null)
                    if (pendingUri != null) {
                        importNotes(pendingUri)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun importNotes(fileUri: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isImporting = true,
                error = null
            )

            val result = importSqliteNotesUseCase.execute(fileUri)

            if (result.isSuccess) {
                // Import successful - clear pending import and reload data from cloud
                _uiState.value = _uiState.value.copy(
                    isImporting = false,
                    pendingImportFileUri = null
                )
                // Notes are now in cloud, so we don't need to reload from sqlite repository
                // The cloud repository will emit updates via observeNotesWithTags() if needed
            } else {
                val exception = result.exceptionOrNull()
                if (exception is LoginRequiredException) {
                    // Store file URI and trigger navigation to login
                    _uiState.value = _uiState.value.copy(
                        isImporting = false,
                        pendingImportFileUri = fileUri
                    )
                    onNavigateToLogin?.invoke()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isImporting = false,
                        error = exception?.message ?: "Import failed"
                    )
                }
            }
        }
    }
}

data class NotesUiState(
    val isLoading: Boolean = true,
    val notes: List<TextNote> = emptyList(),
    val readings: List<Reading> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val noteTags: List<NoteTag> = emptyList(),
    val error: String? = null,
    val isImporting: Boolean = false,
    val pendingImportFileUri: String? = null
)




