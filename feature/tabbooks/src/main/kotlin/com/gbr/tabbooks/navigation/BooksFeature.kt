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
                    onNavigateToDownloader = { navHostController.navigate(SubGraphDest.BooksDownload) },
                    onNavigateToBookDetail = { gitabaseId, bookId ->
                        // Navigate to BookPreview with arguments
                        navHostController.navigate(Dest.BookPreview)
                    }
                )
            }
            
            composable<Dest.BookPreview> {
                // For now, we'll use default values since we need to extract arguments from the route
                // In a real implementation, you'd parse the navigation arguments
                val gitabaseId = com.gbr.model.gitabase.GitabaseID(
                    type = com.gbr.model.gitabase.GitabaseType.HELP,
                    lang = com.gbr.model.gitabase.GitabaseLang.ENG
                )
                val bookId = 1 // Default book ID
                
                com.gbr.scrbook.screen.BookPreviewScreen(
                    gitabaseId = gitabaseId,
                    bookId = bookId,
                    onNavigateBack = { navHostController.popBackStack() }
                )
            }
        }
    }
}
