package com.gbr.scrDownloader.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.gbr.common.navigation.Dest
import com.gbr.common.navigation.SubGraphDest
import com.gbr.common.network.Feature
import com.gbr.scrDownloader.screen.DownloaderScreen
import javax.inject.Inject
import javax.inject.Singleton

interface DownloaderFeature : Feature

@Singleton
class DownloaderFeatureImpl @Inject constructor() : DownloaderFeature {
    override fun registerGraph(
        navHostController: NavHostController,
        navGraphBuilder: NavGraphBuilder
    ) {
        navGraphBuilder.navigation<SubGraphDest.BooksDownload>(startDestination = Dest.BooksDownload) {
            composable<Dest.BooksDownload> {
                DownloaderScreen(
                    onNavigateBack = { navHostController.popBackStack() }
                )
            }
            
            composable<Dest.BooksDownloadDetail> {
                // TODO: Implement BooksDownloadDetailScreen
                DownloaderScreen(
                    onNavigateBack = { navHostController.popBackStack() }
                )
            }
        }
    }
}
