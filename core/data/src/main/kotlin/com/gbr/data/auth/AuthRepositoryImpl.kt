package com.gbr.data.auth

import com.gbr.model.auth.AuthData
import com.gbr.model.auth.AuthResultData
import com.gbr.network.AuthResult
import com.gbr.network.AuthState
import com.gbr.network.AuthUser
import com.gbr.network.IAuthDataSource
import com.gbr.network.IAuthStatusDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: IAuthDataSource,
    private val authStatusDataSource: IAuthStatusDataSource
) : AuthRepository {

    override suspend fun signInWithEmail(email: String, password: String): Result<AuthResultData> {
        return authDataSource.signInWithEmail(email, password).map { authResult ->
            AuthResultData(
                success = authResult.success,
                userId = authResult.userId,
                email = authResult.email,
                error = authResult.error
            )
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String): Result<AuthResultData> {
        return authDataSource.signUpWithEmail(email, password).map { authResult ->
            AuthResultData(
                success = authResult.success,
                userId = authResult.userId,
                email = authResult.email,
                error = authResult.error
            )
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<AuthResultData> {
        return authDataSource.signInWithGoogle(idToken).map { authResult ->
            AuthResultData(
                success = authResult.success,
                userId = authResult.userId,
                email = authResult.email,
                error = authResult.error
            )
        }
    }

    override suspend fun signInWithFacebook(accessToken: String): Result<AuthResultData> {
        return authDataSource.signInWithFacebook(accessToken).map { authResult ->
            AuthResultData(
                success = authResult.success,
                userId = authResult.userId,
                email = authResult.email,
                error = authResult.error
            )
        }
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

    override fun observeAuthState(): Flow<AuthData> {
        return authStatusDataSource.observeAuthState().map { authState: AuthState ->
            AuthData(
                isLoggedIn = authState.isLoggedIn,
                userId = authState.userId,
                email = authState.email
            )
        }
    }
}

