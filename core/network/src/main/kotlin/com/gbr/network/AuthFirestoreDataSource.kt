package com.gbr.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthFirestoreDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : IAuthDataSource {
    
    override suspend fun signInWithEmail(email: String, password: String): Result<AuthResult> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            Result.success(AuthResult(
                success = true,
                userId = user?.uid,
                email = user?.email
            ))
        } catch (e: Exception) {
            Result.success(AuthResult(
                success = false,
                userId = null,
                email = null,
                error = e.message
            ))
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String): Result<AuthResult> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            Result.success(AuthResult(
                success = true,
                userId = user?.uid,
                email = user?.email
            ))
        } catch (e: Exception) {
            Result.success(AuthResult(
                success = false,
                userId = null,
                email = null,
                error = e.message
            ))
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<AuthResult> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user
            Result.success(AuthResult(
                success = true,
                userId = user?.uid,
                email = user?.email
            ))
        } catch (e: Exception) {
            Result.success(AuthResult(
                success = false,
                userId = null,
                email = null,
                error = e.message
            ))
        }
    }

    override suspend fun signInWithFacebook(accessToken: String): Result<AuthResult> {
        return try {
            val credential = FacebookAuthProvider.getCredential(accessToken)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user
            Result.success(AuthResult(
                success = true,
                userId = user?.uid,
                email = user?.email
            ))
        } catch (e: Exception) {
            Result.success(AuthResult(
                success = false,
                userId = null,
                email = null,
                error = e.message
            ))
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): AuthUser? {
        val user = firebaseAuth.currentUser ?: return null
        return AuthUser(
            userId = user.uid,
            email = user.email,
            displayName = user.displayName
        )
    }
}

