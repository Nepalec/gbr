package com.gbr.scrDownloader.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.gbr.scrDownloader.screen.DownloaderScreen

const val DOWNLOADER_ROUTE = "downloader"

@Composable
fun DownloaderNavigation(
    onNavigateBack: () -> Unit
) {
    DownloaderScreen(
        onNavigateBack = onNavigateBack
    )
}

fun NavGraphBuilder.downloaderScreen(
    onNavigateBack: () -> Unit
) {
    composable(DOWNLOADER_ROUTE) {
        DownloaderScreen(
            onNavigateBack = onNavigateBack
        )
    }
}
