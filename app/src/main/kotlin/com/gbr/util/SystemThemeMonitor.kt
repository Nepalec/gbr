package com.gbr.util

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Extension function that creates a Flow to monitor system theme changes.
 * Similar to Now in Android's implementation, this monitors system theme changes
 * and emits the current dark theme state.
 */
fun ComponentActivity.isSystemInDarkTheme(): Flow<Boolean> {
    return callbackFlow {
        val listener = object : android.content.ComponentCallbacks2 {
            override fun onConfigurationChanged(newConfig: Configuration) {
                val nightMode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
                val isDark = nightMode == Configuration.UI_MODE_NIGHT_YES
                trySend(isDark)
            }

            override fun onLowMemory() {}
            override fun onTrimMemory(level: Int) {}
        }

        // Register the listener
        registerComponentCallbacks(listener)

        // Send initial value
        val nightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDark = nightMode == Configuration.UI_MODE_NIGHT_YES
        trySend(isDark)

        // Clean up when the flow is cancelled
        awaitClose {
            unregisterComponentCallbacks(listener)
        }
    }.conflate().distinctUntilChanged()
}
