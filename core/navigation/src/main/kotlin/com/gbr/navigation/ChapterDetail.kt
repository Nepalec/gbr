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
import com.gbr.model.book.TextPreviewItem
import com.gbr.model.gitabase.parseGitabaseID
import com.gbr.scrchapter.screen.ChapterScreen

fun NavGraphBuilder.ChapterDetail(
    navHostController: NavHostController,
    textsRepository: TextsRepository
) {
    composable<FullscreenDest.ChapterDetail> { backStackEntry ->
        val route: FullscreenDest.ChapterDetail = backStackEntry.toRoute()

        // Parse GitabaseID from key
        val gitabaseId = route.gitabaseIdKey.parseGitabaseID()

        // State for loading bookPreview and bookDetail asynchronously
        var bookPreview by remember { mutableStateOf<BookPreview?>(null) }
        var bookDetail by remember { mutableStateOf<com.gbr.model.book.BookDetail?>(null) }
        var isLoading by remember { mutableStateOf(true) }

        // Load bookPreview and bookDetail asynchronously
        LaunchedEffect(route.gitabaseIdKey, route.bookId, route.chapterNumber) {
            isLoading = true
            bookPreview = null
            bookDetail = null

            gitabaseId?.let { id ->
                try {
                    // First get BookPreview
                    val previewResult = textsRepository.getBookPreviewById(id, route.bookId)
                    previewResult.onSuccess { book ->
                        bookPreview = book

                        // Then get BookDetail with extractImages = false
                        book?.let { preview ->
                            val detailResult = textsRepository.getBookDetail(id, preview, extractImages = false)
                            detailResult.onSuccess { detail ->
                                bookDetail = detail
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Error loading
                }
            }

            isLoading = false
        }

        // Show ChapterScreen only when data is loaded successfully
        if (!isLoading && gitabaseId != null && bookPreview != null && bookDetail != null) {
            ChapterScreen(
                gitabaseId = gitabaseId,
                bookPreview = bookPreview!!,
                chapterNumber = route.chapterNumber,
                onNavigateBack = { navHostController.popBackStack() },
                onNavigateToText = { textItem: TextPreviewItem ->
                    navHostController.navigate(
                        FullscreenDest.TextDetail(
                            gitabaseIdKey = route.gitabaseIdKey,
                            bookId = route.bookId,
                            chapterNumber = textItem.chapterNumber,
                            textNumber = textItem.textNumber
                        )
                    )
                }
            )
        }
        // Show nothing while loading or if there's an error
    }
}
