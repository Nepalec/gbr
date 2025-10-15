package com.gbr.tabnotes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            // TODO: Load notes from repository
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                notes = emptyList()
            )
        }
    }

    fun refreshNotes() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadNotes()
    }
}

data class NotesUiState(
    val isLoading: Boolean = true,
    val notes: List<String> = emptyList(),
    val error: String? = null
)




