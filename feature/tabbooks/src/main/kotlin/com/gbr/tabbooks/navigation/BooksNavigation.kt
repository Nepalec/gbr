package com.gbr.tabbooks.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.gbr.tabbooks.screen.BooksScreen
import com.gbr.tabbooks.viewmodel.BooksViewModel

@Composable
fun BooksNavigation(
    onNavigateBack: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    BooksScreen(
        onNavigateBack = onNavigateBack,
        onNavigateToSettings = onNavigateToSettings,
        viewModel = hiltViewModel()
    )
}

