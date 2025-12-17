package com.gbr.data.usecase

/**
 * Exception thrown when a use case requires user authentication but the user is not logged in.
 * This signals that the UI should navigate to the login screen.
 */
class LoginRequiredException : Exception("User must be logged in to perform this action")
