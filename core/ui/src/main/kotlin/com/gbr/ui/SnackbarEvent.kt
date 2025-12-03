package com.gbr.ui

import androidx.compose.material3.SnackbarDuration

data class SnackbarEvent(
    val message: String,
    val actionLabel: String?,
    val duration: SnackbarDuration
)

