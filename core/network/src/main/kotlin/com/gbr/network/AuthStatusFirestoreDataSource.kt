package com.gbr.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthStatusFirestoreDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : IAuthStatusDataSource {

    override suspend fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override fun observeAuthState(): Flow<AuthState> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            val authState = AuthState(
                isLoggedIn = user != null,
                userId = user?.uid,
                email = user?.email
            )
            trySend(authState)
        }
        
        firebaseAuth.addAuthStateListener(listener)
        
        // Send initial state
        val currentUser = firebaseAuth.currentUser
        val initialAuthState = AuthState(
            isLoggedIn = currentUser != null,
            userId = currentUser?.uid,
            email = currentUser?.email
        )
        trySend(initialAuthState)
        
        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }
}

