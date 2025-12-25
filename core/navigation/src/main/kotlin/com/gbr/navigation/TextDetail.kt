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
import com.gbr.data.repository.TextsRepository
import com.gbr.model.book.BookPreview
import com.gbr.model.gitabase.parseGitabaseID
import com.gbr.scrtext.screen.TextScreen

fun NavGraphBuilder.TextDetail(
    navHostController: NavHostController,
    textsRepository: TextsRepository
) {
    composable<FullscreenDest.TextDetail> { backStackEntry ->
        val route: FullscreenDest.TextDetail = backStackEntry.toRoute()

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
                    // Get BookPreview
                    val previewResult = textsRepository.getBookPreviewById(id, route.bookId)
                    previewResult.onSuccess { book ->
                        bookPreview = book
                    }
                } catch (e: Exception) {
                    // Error loading
                }
            }

            isLoading = false
        }

        // Show TextScreen only when data is loaded successfully
        if (!isLoading && gitabaseId != null && bookPreview != null) {
            TextScreen(
                gitabaseId = gitabaseId,
                bookPreview = bookPreview!!,
                chapterNumber = route.chapterNumber,
                textNumber = route.textNumber,
                onNavigateBack = { navHostController.popBackStack() }
            )
        }
        // Show nothing while loading or if there's an error
    }
}
