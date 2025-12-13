package com.gbr.scrLogin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbr.data.auth.AuthRepository
import com.gbr.model.auth.AuthResultData
import com.gbr.ui.SnackbarHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val snackbarHelper: SnackbarHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun signInWithEmail() {
        val email = _uiState.value.email
        val password = _uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            viewModelScope.launch {
                snackbarHelper.showMessage("Please enter email and password")
            }
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.signInWithEmail(email, password)
            result.fold(
                onSuccess = { authResult: AuthResultData ->
                    if (authResult.success) {
                        _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                        snackbarHelper.showMessage("Login successful")
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = authResult.error ?: "Login failed"
                        )
                        snackbarHelper.showMessage(authResult.error ?: "Login failed")
                    }
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Login failed"
                    )
                    snackbarHelper.showMessage(e.message ?: "Login failed")
                }
            )
        }
    }

    fun signUpWithEmail() {
        val email = _uiState.value.email
        val password = _uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            viewModelScope.launch {
                snackbarHelper.showMessage("Please enter email and password")
            }
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.signUpWithEmail(email, password)
            result.fold(
                onSuccess = { authResult: AuthResultData ->
                    if (authResult.success) {
                        _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                        snackbarHelper.showMessage("Account created successfully")
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = authResult.error ?: "Sign up failed"
                        )
                        snackbarHelper.showMessage(authResult.error ?: "Sign up failed")
                    }
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Sign up failed"
                    )
                    snackbarHelper.showMessage(e.message ?: "Sign up failed")
                }
            )
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.signInWithGoogle(idToken)
            result.fold(
                onSuccess = { authResult: AuthResultData ->
                    if (authResult.success) {
                        _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                        snackbarHelper.showMessage("Google sign in successful")
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = authResult.error ?: "Google sign in failed"
                        )
                        snackbarHelper.showMessage(authResult.error ?: "Google sign in failed")
                    }
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Google sign in failed"
                    )
                    snackbarHelper.showMessage(e.message ?: "Google sign in failed")
                }
            )
        }
    }

    fun signInWithFacebook(accessToken: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.signInWithFacebook(accessToken)
            result.fold(
                onSuccess = { authResult: AuthResultData ->
                    if (authResult.success) {
                        _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                        snackbarHelper.showMessage("Facebook sign in successful")
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = authResult.error ?: "Facebook sign in failed"
                        )
                        snackbarHelper.showMessage(authResult.error ?: "Facebook sign in failed")
                    }
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Facebook sign in failed"
                    )
                    snackbarHelper.showMessage(e.message ?: "Facebook sign in failed")
                }
            )
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

