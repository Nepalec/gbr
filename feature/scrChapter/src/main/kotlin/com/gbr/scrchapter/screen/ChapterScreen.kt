package com.gbr.scrchapter.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.gbr.model.book.BookPreview
import com.gbr.model.gitabase.GitabaseID
import com.gbr.scrchapter.R
import com.gbr.scrchapter.screen.components.ChapterBottomSheet
import com.gbr.scrchapter.screen.components.ChapterContents
import com.gbr.scrchapter.screen.components.ChapterTitleBar
import com.gbr.scrchapter.screen.components.DrawerNavigation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterScreen(
    gitabaseId: GitabaseID,
    bookPreview: BookPreview,
    chapterNumber: Int,
    onNavigateBack: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    viewModel: ChapterViewModel = hiltViewModel()
) {
    // Load chapter data when screen is composed
    LaunchedEffect(gitabaseId, bookPreview, chapterNumber) {
        viewModel.loadChapter(gitabaseId, bookPreview, chapterNumber)
    }

    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    // Get current chapter number from state, fallback to parameter
    val currentChapterNumber = uiState.chapter?.number ?: chapterNumber

    // Get saved scroll position for current chapter
    val savedScrollPosition = remember(currentChapterNumber) {
        viewModel.getScrollPosition(currentChapterNumber)
    }

    // Get chapter title from state
    val chapterTitle = uiState.chapter?.title ?: stringResource(R.string.chapter, chapterNumber)
    val bookTitle = uiState.bookDetail?.book?.title ?: bookPreview.title
    val chapters = uiState.bookDetail?.chapters

    // Store reference to LazyListState
    var listState by remember { mutableStateOf<LazyListState?>(null) }

    // Callback to receive LazyListState from ChapterContents
    val onListStateReady = { state: LazyListState ->
        listState = state
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                DrawerNavigation(
                    bookTitle = bookTitle,
                    chapters = chapters,
                    currentChapterNumber = currentChapterNumber,
                    onChapterSelected = { selectedChapterNumber ->
                        scope.launch {
                            drawerState.close()
                        }
                        // Save current chapter's scroll position before switching
                        listState?.let { state ->
                            val currentChapter = uiState.chapter?.number
                            if (currentChapter != null) {
                                viewModel.saveScrollPosition(
                                    currentChapter,
                                    state.firstVisibleItemIndex,
                                    state.firstVisibleItemScrollOffset
                                )
                            }
                        }
                        // Load new chapter
                        viewModel.loadChapter(gitabaseId, bookPreview, selectedChapterNumber)
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                ChapterTitleBar(
                    bookTitle = bookTitle,
                    chapterTitle = chapterTitle,
                    onDrawerClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    onMoreOptionsClick = { showBottomSheet = true }
                )
            },
            containerColor = MaterialTheme.colorScheme.surface
        ) { innerPadding ->
            ChapterContents(
                isLoading = uiState.isLoading,
                error = uiState.error,
                chapter = uiState.chapter,
                bookDetail = uiState.bookDetail,
                chapterTexts = uiState.chapterTexts,
                chapterNumber = currentChapterNumber,
                savedScrollPosition = savedScrollPosition,
                onListStateReady = onListStateReady,
                modifier = Modifier.padding(
                    top = innerPadding.calculateTopPadding()
                )
            )
        }
    }

    // Bottom sheet
    ChapterBottomSheet(
        showBottomSheet = showBottomSheet,
        onDismissRequest = { showBottomSheet = false }
    )
}
