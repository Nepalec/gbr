package com.gbr.ui

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import androidx.compose.material3.SnackbarDuration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnackbarHelper @Inject constructor() {

    private val _events = MutableSharedFlow<SnackbarEvent>()

    val events: SharedFlow<SnackbarEvent> = _events.asSharedFlow()

    suspend fun showMessage(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short
    ) {
        _events.emit(
            SnackbarEvent(
                message = message,
                actionLabel = actionLabel,
                duration = duration
            )
        )
    }
}

