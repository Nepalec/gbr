package com.gbr.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // TODO: Load settings from repository
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                settings = emptyList()
            )
        }
    }

    fun refreshSettings() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadSettings()
    }
}

data class SettingsUiState(
    val isLoading: Boolean = true,
    val settings: List<String> = emptyList(),
    val error: String? = null
)




