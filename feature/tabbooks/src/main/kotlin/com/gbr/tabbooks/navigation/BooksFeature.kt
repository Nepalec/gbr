package com.gbr.tabbooks.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.gbr.common.navigation.Dest
import com.gbr.common.navigation.SubGraphDest
import com.gbr.common.network.Feature
import com.gbr.tabbooks.screen.BooksScreen
import javax.inject.Inject
import javax.inject.Singleton

interface BooksFeature : Feature

@Singleton
class BooksFeatureImpl @Inject constructor() : BooksFeature {
    override fun registerGraph(
        navHostController: NavHostController,
        navGraphBuilder: NavGraphBuilder
    ) {
        navGraphBuilder.navigation<SubGraphDest.Books>(startDestination = Dest.Books) {
            composable<Dest.Books> {
                BooksScreen(
                    onNavigateBack = { navHostController.popBackStack() },
                    onNavigateToSettings = { navHostController.navigate(Dest.Settings) },
                    onNavigateToDownloader = { navHostController.navigate(SubGraphDest.BooksDownload) }
                )
            }
            
            composable<Dest.BookDetail> {
                // TODO: Implement BookDetailScreen
                BooksScreen(
                    onNavigateBack = { navHostController.popBackStack() },
                    onNavigateToSettings = { navHostController.navigate(Dest.Settings) }
                )
            }
        }
    }
}
