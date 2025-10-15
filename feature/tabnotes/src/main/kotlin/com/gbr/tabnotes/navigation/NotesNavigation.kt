package com.gbr.tabnotes.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.gbr.tabnotes.screen.NotesScreen
import com.gbr.tabnotes.viewmodel.NotesViewModel

@Composable
fun NotesNavigation(
    onNavigateBack: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    NotesScreen(
        onNavigateBack = onNavigateBack,
        onNavigateToSettings = onNavigateToSettings,
        viewModel = hiltViewModel()
    )
}




