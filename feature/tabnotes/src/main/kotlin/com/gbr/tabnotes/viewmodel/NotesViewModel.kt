package com.gbr.tabnotes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbr.data.auth.AuthRepository
import com.gbr.data.repository.UserNotesRepository
import com.gbr.data.repository.UserPreferencesRepository
import com.gbr.data.usecase.ImportSqliteNotesUseCase
import com.gbr.data.usecase.LoginRequiredException
import com.gbr.model.notes.NoteTag
import com.gbr.model.notes.NotesStorageMode
import com.gbr.model.notes.Reading
import com.gbr.model.notes.Tag
import com.gbr.model.notes.TextNote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val userNotesRepository: UserNotesRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val authRepository: AuthRepository,
    private val importSqliteNotesUseCase: ImportSqliteNotesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    init {
        observeNotes()
    }

    private fun observeNotes() {
        // Combine notes with tags, storage mode, and auth state flows
        combine(
            userNotesRepository.observeNotesWithTags(),
            userNotesRepository.observeTags(),
            userPreferencesRepository.notesStorageMode,
            authRepository.observeAuthState().map { it.isLoggedIn }
        ) { notesWithTags, allTags, storageMode, isLoggedIn ->
            // Extract notes from TextNoteWithTags
            val notes = notesWithTags.map { it.note }

            // Extract tags (use allTags from observeTags for complete list)
            val tags = allTags

            // Reconstruct noteTags from TextNoteWithTags
            val noteTags = notesWithTags.flatMap { noteWithTags ->
                noteWithTags.tags.map { tag ->
                    NoteTag(
                        id = 0,
                        noteId = noteWithTags.note.id,
                        tagId = tag.id
                    )
                }
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                notes = notes,
                tags = tags,
                noteTags = noteTags,
                notesStorageMode = storageMode,
                isLoggedIn = isLoggedIn
            )
        }.launchIn(viewModelScope)
    }

    fun refreshNotes() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        // Data will automatically update via the flow observers
    }

    fun importNotes(fileUri: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isImporting = true,
                importError = null
            )

            val result = importSqliteNotesUseCase.execute(fileUri)

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isImporting = false
                    )
                    // Data will automatically refresh via observe flows
                },
                onFailure = { e ->
                    if (e is LoginRequiredException) {
                        // Signal that login is needed
                        _uiState.value = _uiState.value.copy(
                        isImporting = false,
                            importError = "LOGIN_REQUIRED"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isImporting = false,
                            importError = e.message ?: "Import failed"
                    )
                }
            }
            )
        }
    }
}

data class NotesUiState(
    val isLoading: Boolean = true,
    val notes: List<TextNote> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val noteTags: List<NoteTag> = emptyList(),
    val notesStorageMode: NotesStorageMode = NotesStorageMode.LOCAL,
    val isLoggedIn: Boolean = false,
    val isImporting: Boolean = false,
    val importError: String? = null
)
