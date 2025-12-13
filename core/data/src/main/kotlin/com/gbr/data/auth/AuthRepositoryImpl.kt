package com.gbr.data.auth

import com.gbr.network.AuthResult
import com.gbr.network.AuthState
import com.gbr.network.AuthUser
import com.gbr.network.IAuthDataSource
import com.gbr.network.IAuthStatusDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: IAuthDataSource,
    private val authStatusDataSource: IAuthStatusDataSource
) : AuthRepository {

    override suspend fun signInWithEmail(email: String, password: String): Result<AuthResult> {
        return authDataSource.signInWithEmail(email, password)
    }

    override suspend fun signUpWithEmail(email: String, password: String): Result<AuthResult> {
        return authDataSource.signUpWithEmail(email, password)
    }

    override suspend fun signInWithGoogle(idToken: String): Result<AuthResult> {
        return authDataSource.signInWithGoogle(idToken)
    }

    override suspend fun signInWithFacebook(accessToken: String): Result<AuthResult> {
        return authDataSource.signInWithFacebook(accessToken)
    }

    override suspend fun signOut(): Result<Unit> {
        return authDataSource.signOut()
    }

    override suspend fun getCurrentUser(): AuthUser? {
        return authDataSource.getCurrentUser()
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return authStatusDataSource.isUserLoggedIn()
    }

    override fun observeAuthState(): Flow<AuthState> {
        return authStatusDataSource.observeAuthState()
    }
}

