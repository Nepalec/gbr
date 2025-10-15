package com.gbr.tabbooks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(BooksUiState())
    val uiState: StateFlow<BooksUiState> = _uiState.asStateFlow()

    init {
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            // TODO: Load books from repository
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                books = emptyList()
            )
        }
    }

    fun refreshBooks() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadBooks()
    }
}

data class BooksUiState(
    val isLoading: Boolean = true,
    val books: List<String> = emptyList(),
    val error: String? = null
)

