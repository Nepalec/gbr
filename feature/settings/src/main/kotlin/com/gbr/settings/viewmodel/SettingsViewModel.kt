package com.gbr.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbr.common.strings.StringProvider
import com.gbr.data.repository.UserPreferencesRepository
import com.gbr.model.theme.DarkThemeConfig
import com.gbr.settings.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val stringProvider: StringProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            try {
                val currentTheme = userPreferencesRepository.appTheme.firstOrNull() ?: DarkThemeConfig.FOLLOW_SYSTEM
                val themeOption = when (currentTheme) {
                    DarkThemeConfig.LIGHT -> ThemeOption.LIGHT
                    DarkThemeConfig.DARK -> ThemeOption.DARK
                    DarkThemeConfig.FOLLOW_SYSTEM -> ThemeOption.SYSTEM
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    selectedTheme = themeOption
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = stringProvider.getString(R.string.error_failed_to_load_settings, e.message ?: ""),
                    selectedTheme = ThemeOption.SYSTEM
                )
            }
        }
    }

    fun refreshSettings() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadSettings()
    }

    fun selectTheme(theme: ThemeOption) {
        viewModelScope.launch {
            try {
                val darkThemeConfig = when (theme) {
                    ThemeOption.LIGHT -> DarkThemeConfig.LIGHT
                    ThemeOption.DARK -> DarkThemeConfig.DARK
                    ThemeOption.SYSTEM -> DarkThemeConfig.FOLLOW_SYSTEM
                }

                // Save the theme preference
                userPreferencesRepository.setAppTheme(darkThemeConfig)

                // Update UI state
                _uiState.value = _uiState.value.copy(selectedTheme = theme)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = stringProvider.getString(R.string.error_failed_to_save_theme, e.message ?: "")
                )
            }
        }
    }
}

enum class ThemeOption {
    LIGHT,
    DARK,
    SYSTEM
}

data class SettingsUiState(
    val isLoading: Boolean = true,
    val selectedTheme: ThemeOption = ThemeOption.SYSTEM,
    val error: String? = null
)




