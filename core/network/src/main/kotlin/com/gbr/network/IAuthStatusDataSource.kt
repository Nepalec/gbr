package com.gbr.network

import kotlinx.coroutines.flow.Flow

interface IAuthStatusDataSource {
    suspend fun getCurrentUserId(): String?
    suspend fun isUserLoggedIn(): Boolean
    fun observeAuthState(): Flow<AuthState>
}

data class AuthState(
    val isLoggedIn: Boolean,
    val userId: String?,
    val email: String?
)

