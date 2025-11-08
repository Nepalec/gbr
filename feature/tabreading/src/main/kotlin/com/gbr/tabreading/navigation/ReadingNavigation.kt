package com.gbr.tabreading.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.gbr.tabreading.screen.ReadingScreen

@Composable
fun ReadingNavigation(
    onNavigateBack: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    ReadingScreen(
        onNavigateBack = onNavigateBack,
        onNavigateToSettings = onNavigateToSettings,
        viewModel = hiltViewModel()
    )
}

