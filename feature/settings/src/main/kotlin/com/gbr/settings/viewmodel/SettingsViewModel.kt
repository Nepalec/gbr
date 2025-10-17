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
                selectedTheme = ThemeOption.SYSTEM
            )
        }
    }

    fun refreshSettings() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadSettings()
    }

    fun selectTheme(theme: ThemeOption) {
        _uiState.value = _uiState.value.copy(selectedTheme = theme)
    }
}

enum class ThemeOption(val displayName: String) {
    LIGHT("Light"),
    DARK("Dark"),
    SYSTEM("System")
}

data class SettingsUiState(
    val isLoading: Boolean = true,
    val selectedTheme: ThemeOption = ThemeOption.SYSTEM,
    val error: String? = null
)




