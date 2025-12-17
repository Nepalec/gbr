package com.gbr.scrLogin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbr.common.strings.StringProvider
import com.gbr.data.auth.AuthRepository
import com.gbr.model.auth.AuthResultData
import com.gbr.scrLogin.R
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
    private val snackbarHelper: SnackbarHelper,
    private val stringProvider: StringProvider
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

    fun showForgotPasswordDialog() {
        // Pre-fill email from login form if available
        val emailToUse = if (_uiState.value.forgotPasswordEmail.isBlank()) {
            _uiState.value.email
        } else {
            _uiState.value.forgotPasswordEmail
        }
        _uiState.value = _uiState.value.copy(
            showForgotPasswordDialog = true,
            forgotPasswordEmail = emailToUse
        )
    }

    fun dismissForgotPasswordDialog() {
        _uiState.value = _uiState.value.copy(
            showForgotPasswordDialog = false,
            forgotPasswordEmail = "",
            forgotPasswordError = null,
            isSendingResetEmail = false,
            forgotPasswordEmailSent = false
        )
    }

    fun updateForgotPasswordEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            forgotPasswordEmail = email,
            forgotPasswordError = null
        )
    }

    fun sendPasswordResetEmail() {
        val email = _uiState.value.forgotPasswordEmail.trim()

        if (email.isBlank()) {
            viewModelScope.launch {
                snackbarHelper.showMessage(stringProvider.getString(R.string.error_please_enter_email))
            }
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            viewModelScope.launch {
                snackbarHelper.showMessage(stringProvider.getString(R.string.error_please_enter_valid_email))
            }
            _uiState.value = _uiState.value.copy(forgotPasswordError = stringProvider.getString(R.string.error_invalid_email_format))
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSendingResetEmail = true,
                forgotPasswordError = null
            )

            val result = authRepository.sendPasswordResetEmail(email)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isSendingResetEmail = false,
                        forgotPasswordEmailSent = true
                    )
                    snackbarHelper.showMessage(stringProvider.getString(R.string.success_password_reset_sent))
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isSendingResetEmail = false,
                        forgotPasswordError = e.message ?: stringProvider.getString(R.string.error_failed_to_send_reset_email)
                    )
                    snackbarHelper.showMessage(e.message ?: stringProvider.getString(R.string.error_failed_to_send_reset_email))
                }
            )
        }
    }

    fun signInWithEmail() {
        val email = _uiState.value.email
        val password = _uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            viewModelScope.launch {
                snackbarHelper.showMessage(stringProvider.getString(R.string.error_please_enter_email_and_password))
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
                        snackbarHelper.showMessage(stringProvider.getString(R.string.success_login))
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = authResult.error ?: stringProvider.getString(R.string.error_login_failed)
                        )
                        snackbarHelper.showMessage(authResult.error ?: stringProvider.getString(R.string.error_login_failed))
                    }
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: stringProvider.getString(R.string.error_login_failed)
                    )
                    snackbarHelper.showMessage(e.message ?: stringProvider.getString(R.string.error_login_failed))
                }
            )
        }
    }

    fun signUpWithEmail() {
        val email = _uiState.value.email
        val password = _uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            viewModelScope.launch {
                snackbarHelper.showMessage(stringProvider.getString(R.string.error_please_enter_email_and_password))
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
                        snackbarHelper.showMessage(stringProvider.getString(R.string.success_account_created))
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = authResult.error ?: stringProvider.getString(R.string.error_sign_up_failed)
                        )
                        snackbarHelper.showMessage(authResult.error ?: stringProvider.getString(R.string.error_sign_up_failed))
                    }
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: stringProvider.getString(R.string.error_sign_up_failed)
                    )
                    snackbarHelper.showMessage(e.message ?: stringProvider.getString(R.string.error_sign_up_failed))
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
                        snackbarHelper.showMessage(stringProvider.getString(R.string.success_google_sign_in))
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = authResult.error ?: stringProvider.getString(R.string.error_google_sign_in_failed)
                        )
                        snackbarHelper.showMessage(authResult.error ?: stringProvider.getString(R.string.error_google_sign_in_failed))
                    }
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: stringProvider.getString(R.string.error_google_sign_in_failed)
                    )
                    snackbarHelper.showMessage(e.message ?: stringProvider.getString(R.string.error_google_sign_in_failed))
                }
            )
        }
    }

    fun handleGoogleSignInError(errorMessage: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = errorMessage
            )
            snackbarHelper.showMessage(errorMessage)
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
                        snackbarHelper.showMessage(stringProvider.getString(R.string.success_facebook_sign_in))
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = authResult.error ?: stringProvider.getString(R.string.error_facebook_sign_in_failed)
                        )
                        snackbarHelper.showMessage(authResult.error ?: stringProvider.getString(R.string.error_facebook_sign_in_failed))
                    }
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: stringProvider.getString(R.string.error_facebook_sign_in_failed)
                    )
                    snackbarHelper.showMessage(e.message ?: stringProvider.getString(R.string.error_facebook_sign_in_failed))
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
    val isSuccess: Boolean = false,
    val showForgotPasswordDialog: Boolean = false,
    val forgotPasswordEmail: String = "",
    val forgotPasswordError: String? = null,
    val isSendingResetEmail: Boolean = false,
    val forgotPasswordEmailSent: Boolean = false
)

