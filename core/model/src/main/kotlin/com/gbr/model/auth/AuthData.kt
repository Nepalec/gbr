package com.gbr.model.auth

/**
 * Domain model for authentication state.
 * Equivalent to AuthState from network module but defined in model module
 * to avoid exposing network layer types to feature modules.
 */
data class AuthData(
    val isLoggedIn: Boolean,
    val userId: String?,
    val email: String?
)

