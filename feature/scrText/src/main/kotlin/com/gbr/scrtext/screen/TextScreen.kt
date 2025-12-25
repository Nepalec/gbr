package com.gbr.scrtext.screen

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.gbr.model.book.BookPreview
import com.gbr.model.gitabase.GitabaseID
import com.gbr.scrtext.R
import com.gbr.scrtext.screen.components.TextTopBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextScreen(
    gitabaseId: GitabaseID,
    bookPreview: BookPreview,
    chapterNumber: Int?,
    textNumber: String,
    onNavigateBack: () -> Unit = {},
    viewModel: TextViewModel = hiltViewModel()
) {
    // Initialize view model when screen is composed
    LaunchedEffect(gitabaseId, bookPreview, chapterNumber, textNumber) {
        viewModel.initialize(gitabaseId, bookPreview, chapterNumber, textNumber)
    }

    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()


    // Create pager state
    val pagerState = rememberPagerState(
        initialPage = uiState.initialTextIndex ?: 0,
        pageCount = { if (uiState.totalTextCount > 0) uiState.totalTextCount else 1 }
    )

    // Update pager when initial index changes
    LaunchedEffect(uiState.initialTextIndex) {
        uiState.initialTextIndex?.let { index ->
            if (pagerState.currentPage != index) {
                pagerState.animateScrollToPage(index)
            }
        }
    }

    // Handle page changes
    LaunchedEffect(pagerState.currentPage) {
        viewModel.onPageChanged(pagerState.currentPage)
    }

    val bookTitle = uiState.bookPreview?.title ?: bookPreview.title
    val bookDetail = uiState.bookDetail

    // Calculate subtitle based on current page
    val currentText = uiState.loadedTexts[pagerState.currentPage]
    val subtitle = if (currentText != null) {
        val chapters = bookDetail?.chapters
        if (chapters != null) {
            // Book has chapters
            val chapter = chapters.find { it.number == currentText.preview.chapterNumber }
            val chapterName = chapter?.title ?: ""
            "${currentText.preview.chapterNumber}.${currentText.preview.textNumber} $chapterName"
        } else {
            // Book without chapters
            "${currentText.preview.textNumber} ${currentText.preview.title}"
        }
    } else {
        "" // Empty while loading
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Drawer content can be added later if needed
                Text("Navigation drawer")
            }
        }
    ) {
        Scaffold(
            topBar = {
                TextTopBar(
                    title = bookTitle,
                    subtitle = subtitle,
                    onDrawerClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            },
            containerColor = MaterialTheme.colorScheme.surface
        ) { innerPadding ->
            if (uiState.isLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = uiState.error ?: "Error")
                }
            } else if (uiState.totalTextCount == 0) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "No texts found")
                }
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) { page ->
                    val text = uiState.loadedTexts[page]

                    if (text != null) {
                        // Text is loaded, display it
                        TextPageContent(text = text, viewModel = viewModel)
                    } else {
                        // Text not loaded, show loading and trigger load
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                            LaunchedEffect(page) {
                                viewModel.loadTextsForIndex(page, gitabaseId)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TextPageContent(
    text: com.gbr.model.book.TextDetailItem,
    viewModel: TextViewModel
) {
    val html = remember(text) {
        viewModel.getTextHtml(text)
    }

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = false
                loadDataWithBaseURL(
                    null,
                    html,
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        },
        update = { webView ->
            // Update WebView when text changes
            val newHtml = viewModel.getTextHtml(text)
            webView.loadDataWithBaseURL(
                null,
                newHtml,
                "text/html",
                "UTF-8",
                null
            )
        },
        modifier = Modifier.fillMaxSize()
    )
}
