package com.gbr.tabprofile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            // TODO: Load profile from repository
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                profile = null
            )
        }
    }

    fun refreshProfile() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadProfile()
    }
}

data class ProfileUiState(
    val isLoading: Boolean = true,
    val profile: String? = null,
    val error: String? = null
)




