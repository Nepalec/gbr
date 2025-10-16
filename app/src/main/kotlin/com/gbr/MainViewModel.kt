package com.gbr

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbr.data.usecase.InitializeGitabasesUseCase
import com.gbr.data.repository.GitabasesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val initializeGitabasesUseCase: InitializeGitabasesUseCase
) : ViewModel() {

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _message = MutableStateFlow<String?>("Scanning for Gitabase files...")
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        initializeApp()
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
