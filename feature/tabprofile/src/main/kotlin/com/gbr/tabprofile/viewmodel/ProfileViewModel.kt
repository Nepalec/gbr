package com.gbr.tabprofile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbr.data.auth.AuthRepository
import com.gbr.model.auth.AuthData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        // Observe auth state changes
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            authRepository.observeAuthState().collect { authData: AuthData ->
                val username = authData.email ?: "User"
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = authData.isLoggedIn,
                    username = if (authData.isLoggedIn) username else null
                )
            }
        }
    }

    fun refreshProfile() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadProfile()
    }
}

data class ProfileUiState(
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false,
    val username: String? = null,
    val profile: String? = null,
    val error: String? = null
)




