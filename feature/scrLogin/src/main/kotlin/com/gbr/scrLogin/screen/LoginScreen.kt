package com.gbr.scrLogin.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.gbr.designsystem.R
import com.gbr.scrLogin.R as LoginR
import com.gbr.scrLogin.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface GoogleSignInClientEntryPoint {
    fun googleSignInClient(): GoogleSignInClient
}

@Composable
fun LoginScreen(
    onNavigateBack: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Get GoogleSignInClient via Hilt EntryPoint
    val context = LocalContext.current
    val applicationContext = context.applicationContext
    val entryPoint = EntryPointAccessors.fromApplication(
        applicationContext,
        GoogleSignInClientEntryPoint::class.java
    )
    val googleSignInClient = entryPoint.googleSignInClient()

    // Google Sign-In launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { idToken ->
                viewModel.signInWithGoogle(idToken)
            } ?: run {
                viewModel.handleGoogleSignInError(context.getString(LoginR.string.error_failed_to_get_id_token))
            }
        } catch (e: ApiException) {
            val errorMessage = when (e.statusCode) {
                12501 -> context.getString(LoginR.string.error_google_sign_in_cancelled)
                10 -> context.getString(LoginR.string.error_google_sign_in_developer_error)
                7 -> context.getString(LoginR.string.error_google_sign_in_network_error)
                else -> context.getString(LoginR.string.error_google_sign_in_unknown)
            }
            viewModel.handleGoogleSignInError(errorMessage)
        } catch (e: Exception) {
            viewModel.handleGoogleSignInError(context.getString(LoginR.string.error_google_sign_in_unknown))
        }
    }

    // Navigate back on successful login
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onNavigateBack()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // App Logo placeholder - using existing icon
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.login_24px),
                    contentDescription = stringResource(LoginR.string.cd_app_logo),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(top = 48.dp)
                        .size(48.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    text = stringResource(LoginR.string.login_welcome_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                )

                Text(
                    text = stringResource(LoginR.string.login_welcome_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp)
                )

                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { viewModel.updateEmail(it) },
                    label = {
                        Text(text = stringResource(LoginR.string.login_email))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.mail_24px),
                            contentDescription = stringResource(LoginR.string.cd_email)
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                )

                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { viewModel.updatePassword(it) },
                    visualTransformation = if (uiState.isPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    label = {
                        Text(text = stringResource(LoginR.string.login_password))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.key_24px),
                            contentDescription = stringResource(LoginR.string.cd_password)
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { viewModel.togglePasswordVisibility() }
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(
                                    if (uiState.isPasswordVisible) {
                                        R.drawable.visibility_off_24px
                                    } else {
                                        R.drawable.visibility_24px
                                    }
                                ),
                                contentDescription = if (uiState.isPasswordVisible) {
                                    stringResource(LoginR.string.login_hide_password)
                                } else {
                                    stringResource(LoginR.string.login_show_password)
                                }
                            )
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    enabled = !uiState.isLoading
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = { viewModel.showForgotPasswordDialog() },
                        enabled = !uiState.isLoading
                    ) {
                        Text(text = stringResource(LoginR.string.login_forgot_password))
                    }
                }

                Button(
                    onClick = { viewModel.signInWithEmail() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(text = stringResource(LoginR.string.login_button))
                    }
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider()
                    Text(
                        text = stringResource(LoginR.string.login_or_continue_with),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .background(color = MaterialTheme.colorScheme.surface)
                            .padding(horizontal = 16.dp, vertical = 24.dp)
                    )
                }

                OutlinedButton(
                    onClick = {
                        val signInIntent = googleSignInClient.signInIntent
                        googleSignInLauncher.launch(signInIntent)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                ) {
                    Icon(
                        painter = painterResource(R.drawable.google_g_logo_24px),
                        contentDescription = stringResource(LoginR.string.cd_google),
                        tint = Color.Unspecified,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(LoginR.string.login_continue_with_google),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                OutlinedButton(
                    onClick = {
                        // TODO: Implement Facebook Sign-In
                        // For now, this is a placeholder
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    enabled = !uiState.isLoading
                ) {
                    Icon(
                        painter = painterResource(R.drawable.facebook_logo_24px),
                        contentDescription = stringResource(LoginR.string.cd_facebook),
                        tint = Color.Unspecified,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(LoginR.string.login_continue_with_facebook),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = stringResource(LoginR.string.login_no_account),
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(
                    onClick = { viewModel.signUpWithEmail() },
                    enabled = !uiState.isLoading
                ) {
                    Text(text = stringResource(LoginR.string.login_create_account))
                }
            }
        }
    }

    // Forgot Password Dialog
    if (uiState.showForgotPasswordDialog) {
        ForgotPasswordDialog(
            email = uiState.forgotPasswordEmail,
            onEmailChange = { viewModel.updateForgotPasswordEmail(it) },
            onDismiss = { viewModel.dismissForgotPasswordDialog() },
            onSendResetEmail = { viewModel.sendPasswordResetEmail() },
            isLoading = uiState.isSendingResetEmail,
            error = uiState.forgotPasswordError,
            emailSent = uiState.forgotPasswordEmailSent,
            onEmailSentDismiss = { viewModel.dismissForgotPasswordDialog() }
        )
    }
}

@Composable
fun ForgotPasswordDialog(
    email: String,
    onEmailChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSendResetEmail: () -> Unit,
    isLoading: Boolean,
    error: String?,
    emailSent: Boolean,
    onEmailSentDismiss: () -> Unit
) {
    if (emailSent) {
        AlertDialog(
            onDismissRequest = onEmailSentDismiss,
            title = {
                Text(
                    text = stringResource(LoginR.string.forgot_password_check_email_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(LoginR.string.forgot_password_email_sent),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(LoginR.string.forgot_password_check_instructions),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = onEmailSentDismiss) {
                    Text(stringResource(LoginR.string.forgot_password_ok))
                }
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = stringResource(LoginR.string.forgot_password_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(LoginR.string.forgot_password_message),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        label = { Text(stringResource(LoginR.string.login_email)) },
                        leadingIcon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.mail_24px),
                                contentDescription = stringResource(LoginR.string.cd_email)
                            )
                        },
                        isError = error != null,
                        supportingText = if (error != null) {
                            { Text(error) }
                        } else null,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onSendResetEmail,
                    enabled = !isLoading && email.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(stringResource(LoginR.string.forgot_password_send_link))
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss,
                    enabled = !isLoading
                ) {
                    Text(stringResource(LoginR.string.forgot_password_cancel))
                }
            }
        )
    }
}

