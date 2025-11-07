package com.gbr.scrchapter.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.gbr.model.book.BookPreview
import com.gbr.model.gitabase.GitabaseID
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

    // Track current selected chapter number
    var currentChapterNumber by remember { mutableStateOf(chapterNumber) }

    // Update current chapter when chapterNumber parameter changes
    LaunchedEffect(chapterNumber) {
        currentChapterNumber = chapterNumber
    }

    // Get chapter title from state
    val chapterTitle = uiState.chapter?.title ?: "Chapter $currentChapterNumber"
    val bookTitle = uiState.bookDetail?.book?.title ?: bookPreview.title
    val chapters = uiState.bookDetail?.chapters

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
                        // Update current chapter and reload data
                        currentChapterNumber = selectedChapterNumber
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
                modifier = Modifier.padding(innerPadding)
            )
        }
    }

    // Bottom sheet
    ChapterBottomSheet(
        showBottomSheet = showBottomSheet,
        onDismissRequest = { showBottomSheet = false }
    )
}
