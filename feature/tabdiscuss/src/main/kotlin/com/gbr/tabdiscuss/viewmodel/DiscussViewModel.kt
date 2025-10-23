package com.gbr.tabdiscuss.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscussViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DiscussUiState())
    val uiState: StateFlow<DiscussUiState> = _uiState.asStateFlow()

    init {
        loadDiscussItems()
    }

    private fun loadDiscussItems() {
        viewModelScope.launch {
            // TODO: Load discuss items from repository
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                discussItems = emptyList()
            )
        }
    }

    fun refreshDiscussItems() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadDiscussItems()
    }
}

data class DiscussUiState(
    val isLoading: Boolean = true,
    val discussItems: List<String> = emptyList(),
    val error: String? = null
)
