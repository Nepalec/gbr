package com.gbr.data.auth

import com.gbr.model.auth.AuthData
import com.gbr.model.auth.AuthResultData
import com.gbr.network.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signInWithEmail(email: String, password: String): Result<AuthResultData>
    suspend fun signUpWithEmail(email: String, password: String): Result<AuthResultData>
    suspend fun signInWithGoogle(idToken: String): Result<AuthResultData>
    suspend fun signInWithFacebook(accessToken: String): Result<AuthResultData>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUser(): AuthUser?
    suspend fun isUserLoggedIn(): Boolean
    fun observeAuthState(): Flow<AuthData>
}

