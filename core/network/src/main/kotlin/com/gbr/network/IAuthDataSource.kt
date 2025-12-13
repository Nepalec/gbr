package com.gbr.network

interface IAuthDataSource {
    suspend fun signInWithEmail(email: String, password: String): Result<AuthResult>
    suspend fun signUpWithEmail(email: String, password: String): Result<AuthResult>
    suspend fun signInWithGoogle(idToken: String): Result<AuthResult>
    suspend fun signInWithFacebook(accessToken: String): Result<AuthResult>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUser(): AuthUser?
}

data class AuthResult(
    val success: Boolean,
    val userId: String?,
    val email: String?,
    val error: String? = null
)

data class AuthUser(
    val userId: String,
    val email: String?,
    val displayName: String?
)

