package com.gbr.data.auth

import com.gbr.network.AuthResult
import com.gbr.network.AuthState
import com.gbr.network.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signInWithEmail(email: String, password: String): Result<AuthResult>
    suspend fun signUpWithEmail(email: String, password: String): Result<AuthResult>
    suspend fun signInWithGoogle(idToken: String): Result<AuthResult>
    suspend fun signInWithFacebook(accessToken: String): Result<AuthResult>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUser(): AuthUser?
    suspend fun isUserLoggedIn(): Boolean
    fun observeAuthState(): Flow<AuthState>
}

