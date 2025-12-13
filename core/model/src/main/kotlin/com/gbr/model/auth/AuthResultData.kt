package com.gbr.model.auth

/**
 * Domain model for authentication result.
 * Equivalent to AuthResult from network module but defined in model module
 * to avoid exposing network layer types to feature modules.
 */
data class AuthResultData(
    val success: Boolean,
    val userId: String?,
    val email: String?,
    val error: String? = null
)

