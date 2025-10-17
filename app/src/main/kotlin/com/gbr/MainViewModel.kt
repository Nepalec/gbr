package com.gbr

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbr.data.usecase.InitializeGitabasesUseCase
import com.gbr.data.repository.GitabasesRepository
import com.gbr.data.repository.UserPreferencesRepository
import com.gbr.model.theme.DarkThemeConfig
import com.gbr.util.isSystemInDarkTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val initializeGitabasesUseCase: InitializeGitabasesUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _message = MutableStateFlow<String?>("Scanning for Gitabase files...")
    val message: StateFlow<String?> = _message.asStateFlow()

    // Theme state management
    private var _activity: ComponentActivity? = null
    private val _isSystemDarkTheme = MutableStateFlow(false)
    
    val shouldUseDarkTheme: StateFlow<Boolean> = combine(
        userPreferencesRepository.getAppThemeFlow(),
        _isSystemDarkTheme
    ) { themeConfig, isSystemDark ->
        when (themeConfig) {
            DarkThemeConfig.DARK -> true
            DarkThemeConfig.LIGHT -> false
            DarkThemeConfig.FOLLOW_SYSTEM -> isSystemDark
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    init {
        initializeApp()
    }
    
    fun setActivity(activity: ComponentActivity) {
        _activity = activity
        // Start monitoring system theme changes
        viewModelScope.launch {
            activity.isSystemInDarkTheme().collect { isDark ->
                _isSystemDarkTheme.value = isDark
            }
        }
    }

    private fun initializeApp() {
        viewModelScope.launch {
            try {
                val result = initializeGitabasesUseCase.execute()
                
                _isInitialized.value = true
                _isLoading.value = false
                _message.value = null
            } catch (e: Exception) {
                _isInitialized.value = true
                _isLoading.value = false
                _message.value = "Error: ${e.message}"
            }
        }
    }
}
