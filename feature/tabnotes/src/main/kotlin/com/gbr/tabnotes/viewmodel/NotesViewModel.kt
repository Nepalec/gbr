package com.gbr.tabnotes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbr.data.repository.SqliteNotesRepository
import com.gbr.data.usecase.ImportSqliteNotesUseCase
import com.gbr.model.notes.NoteTag
import com.gbr.model.notes.Reading
import com.gbr.model.notes.Tag
import com.gbr.model.notes.TextNote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val importSqliteNotesUseCase: ImportSqliteNotesUseCase,
    private val sqliteNotesRepository: SqliteNotesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                notes = sqliteNotesRepository.getNotes(),
                readings = sqliteNotesRepository.getReadings(),
                tags = sqliteNotesRepository.getTags(),
                noteTags = sqliteNotesRepository.getNoteTags()
            )
        }
    }

    fun refreshNotes() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadNotes()
    }

    fun importNotes(fileUri: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isImporting = true,
                error = null
            )

            val result = importSqliteNotesUseCase.execute(fileUri)

            if (result.isSuccess) {
                // Reload data from repository
                _uiState.value = _uiState.value.copy(
                    isImporting = false,
                    notes = sqliteNotesRepository.getNotes(),
                    readings = sqliteNotesRepository.getReadings(),
                    tags = sqliteNotesRepository.getTags(),
                    noteTags = sqliteNotesRepository.getNoteTags()
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isImporting = false,
                    error = result.exceptionOrNull()?.message ?: "Import failed"
                )
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
    val isImporting: Boolean = false
)




