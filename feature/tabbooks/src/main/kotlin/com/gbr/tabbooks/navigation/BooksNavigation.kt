package com.gbr.tabbooks.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.gbr.scrDownloader.navigation.DownloaderNavigation
import com.gbr.tabbooks.screen.BooksScreen
import com.gbr.tabbooks.viewmodel.BooksViewModel

@Composable
fun BooksNavigation(
    onNavigateBack: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    var showDownloader by remember { mutableStateOf(false) }

    if (showDownloader) {
        DownloaderNavigation(
            onNavigateBack = { showDownloader = false }
        )
    } else {
        BooksScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToSettings = onNavigateToSettings,
            onNavigateToDownloader = { showDownloader = true },
            viewModel = hiltViewModel()
        )
    }
}

