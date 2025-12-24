package com.gbr.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbr.common.strings.StringProvider
import com.gbr.data.auth.AuthRepository
import com.gbr.data.repository.UserPreferencesRepository
import com.gbr.model.notes.NotesStorageMode
import com.gbr.model.theme.DarkThemeConfig
import com.gbr.settings.R
import com.gbr.ui.SnackbarHelper
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
    private val stringProvider: StringProvider,
    private val authRepository: AuthRepository,
    private val snackbarHelper: SnackbarHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
        observeAuthStatus()
    }

    private fun observeAuthStatus() {
        viewModelScope.launch {
            authRepository.observeAuthState().collect { authData ->
                _uiState.value = _uiState.value.copy(isLoggedIn = authData.isLoggedIn)
            }
        }
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

                val currentStorageMode = userPreferencesRepository.notesStorageMode.firstOrNull() ?: NotesStorageMode.LOCAL

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    selectedTheme = themeOption,
                    notesStorageMode = currentStorageMode
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = stringProvider.getString(R.string.error_failed_to_load_settings, e.message ?: ""),
                    selectedTheme = ThemeOption.SYSTEM,
                    notesStorageMode = NotesStorageMode.LOCAL
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

    fun selectNotesStorageMode(mode: NotesStorageMode) {
        viewModelScope.launch {
            try {
                userPreferencesRepository.setNotesStorageMode(mode)
                _uiState.value = _uiState.value.copy(notesStorageMode = mode)
            } catch (e: Exception) {
                snackbarHelper.showMessage("Failed to save storage mode: ${e.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                val result = authRepository.signOut()
                result.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(isLoggedIn = false)
                        snackbarHelper.showMessage("Logged out successfully")
                    },
                    onFailure = { e ->
                        snackbarHelper.showMessage("Failed to log out: ${e.message}")
                    }
                )
            } catch (e: Exception) {
                snackbarHelper.showMessage("Failed to log out: ${e.message}")
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
    val notesStorageMode: NotesStorageMode = NotesStorageMode.LOCAL,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)
