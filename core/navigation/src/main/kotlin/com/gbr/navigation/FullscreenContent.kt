@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package com.gbr.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.gbr.common.navigation.FullscreenDest
import com.gbr.common.network.Feature
import com.gbr.data.repository.TextsRepository
import com.gbr.model.book.BookPreview
import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.gitabase.GitabaseLang
import com.gbr.model.gitabase.GitabaseType
import com.gbr.model.gitabase.parseGitabaseID
import com.gbr.scrbook.screen.BookDetailScreen
import javax.inject.Inject
import javax.inject.Singleton

interface FullscreenContent : Feature

@Singleton
class FullscreenContentImpl @Inject constructor(
    private val textsRepository: TextsRepository
) : FullscreenContent {
    override fun registerGraph(
        navHostController: NavHostController,
        navGraphBuilder: NavGraphBuilder
    ) {
        navGraphBuilder.composable<FullscreenDest.BookDetail> { backStackEntry ->
            val route: FullscreenDest.BookDetail = backStackEntry.toRoute()

            // Parse GitabaseID from key
            val gitabaseId = route.gitabaseIdKey.parseGitabaseID()

            // State for loading bookPreview asynchronously
            var bookPreview by remember { mutableStateOf<BookPreview?>(null) }
            var isLoading by remember { mutableStateOf(true) }

            // Load bookPreview asynchronously
            LaunchedEffect(route.gitabaseIdKey, route.bookId) {
                isLoading = true
                bookPreview = null

                gitabaseId?.let { id ->
                    try {
                        val result = textsRepository.getBookPreviewById(id, route.bookId)
                        result.onSuccess { book ->
                            bookPreview = book
                        }
                        // onFailure - bookPreview remains null
                    } catch (e: Exception) {
                        // Error loading - bookPreview remains null
                    }
                }

                isLoading = false
            }

            // Show BookDetailScreen only when data is loaded successfully
            if (!isLoading && gitabaseId != null && bookPreview != null) {
                BookDetailScreen(
                    gitabaseId = gitabaseId,
                    bookPreview = bookPreview!!,
                    onNavigateBack = { navHostController.popBackStack() }
                )
            }
            // Show nothing while loading or if there's an error
        }

        navGraphBuilder.composable<FullscreenDest.ChapterDetail> { backStackEntry ->
            val route: FullscreenDest.ChapterDetail = backStackEntry.toRoute()

            // TODO: Implement ChapterDetail screen
            // Placeholder for now - structured only, no implementation
        }
    }

}

