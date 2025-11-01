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
                    onNavigateToBookDetail = { gitabaseId, bookPreview ->
                        // Set the selected book in global state
                        BookNavigationState.setSelectedBook(gitabaseId, bookPreview)
                        // Navigate to BookPreview
                        navHostController.navigate(Dest.BookDetail)
                    }
                )
            }

            composable<Dest.BookDetail> {
                // Get the selected book from global state
                val selectedBook = BookNavigationState.getSelectedBook()
                val gitabaseId = selectedBook?.gitabaseId ?: com.gbr.model.gitabase.GitabaseID(
                    type = com.gbr.model.gitabase.GitabaseType.HELP,
                    lang = com.gbr.model.gitabase.GitabaseLang.ENG
                )
                val bookPreview = selectedBook?.bookPreview ?: com.gbr.model.book.BookPreview(
                    id = 1,
                    sort = 0,
                    title = "Default Book",
                    author = "Unknown Author",
                    description = null,
                    type = "unknown",
                    level = 3,
                    structure = com.gbr.model.book.BookStructure.CHAPTERS,
                    colorBack = null,
                    colorFore = null,
                    volumeGroupTitle = null,
                    volumeGroupAbbrev = null,
                    volumeGroupSort = null,
                    volumeGroupId = null,
                    volumeNumber = null,
                    hasSanskrit = false,
                    isSimple = false,
                    code = ""
                )

                com.gbr.scrbook.screen.BookDetailScreen(
                    gitabaseId = gitabaseId,
                    bookPreview = bookPreview,
                    onNavigateBack = {
                        BookNavigationState.clearSelectedBook()
                        navHostController.popBackStack()
                    }
                )
            }
        }
    }
}
