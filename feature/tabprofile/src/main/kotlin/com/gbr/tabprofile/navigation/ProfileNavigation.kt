package com.gbr.tabprofile.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.gbr.tabprofile.screen.ProfileScreen
import com.gbr.tabprofile.viewmodel.ProfileViewModel

@Composable
fun ProfileNavigation(
    onNavigateBack: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    ProfileScreen(
        onNavigateBack = onNavigateBack,
        onNavigateToSettings = onNavigateToSettings,
        viewModel = hiltViewModel()
    )
}




