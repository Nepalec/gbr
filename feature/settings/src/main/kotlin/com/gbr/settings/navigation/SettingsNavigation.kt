package com.gbr.settings.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.gbr.settings.screen.SettingsScreen
import com.gbr.settings.viewmodel.SettingsViewModel

@Composable
fun SettingsNavigation(
    onNavigateBack: () -> Unit = {}
) {
    SettingsScreen(
        onNavigateBack = onNavigateBack,
        viewModel = hiltViewModel()
    )
}




