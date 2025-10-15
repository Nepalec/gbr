package com.gbr.tabreading.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadingViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ReadingUiState())
    val uiState: StateFlow<ReadingUiState> = _uiState.asStateFlow()

    init {
        loadReadingItems()
    }

    private fun loadReadingItems() {
        viewModelScope.launch {
            // TODO: Load reading items from repository
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                readingItems = emptyList()
            )
        }
    }

    fun refreshReadingItems() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadReadingItems()
    }
}

data class ReadingUiState(
    val isLoading: Boolean = true,
    val readingItems: List<String> = emptyList(),
    val error: String? = null
)

