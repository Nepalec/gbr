package com.gbr.scrbook.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.gbr.model.book.BookDetail
import com.gbr.model.book.BookImageTab
import com.gbr.model.book.BookImagesTabOptions
import com.gbr.model.book.BookPreview
import com.gbr.model.gitabase.GitabaseID
import com.gbr.scrbook.screen.components.AuthorTitleTabsRow
import com.gbr.scrbook.screen.components.TabBookContents
import com.gbr.scrbook.screen.components.ErrorState
import com.gbr.scrbook.screen.components.HEADER_ROW_HEIGHT
import com.gbr.scrbook.screen.components.HeaderRow
import com.gbr.scrbook.screen.components.TabBookImages
import com.gbr.scrbook.screen.components.LoadingState
import com.gbr.scrbook.screen.components.TabOptionsSheet

@Composable
fun BookDetailScreen(
    gitabaseId: GitabaseID,
    bookPreview: BookPreview,
    onNavigateBack: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNavigateToChapter: (Int) -> Unit = {}, // chapterNumber
    viewModel: BookDetailViewModel = hiltViewModel()
) {
    // Load book data when screen is composed
    LaunchedEffect(gitabaseId, bookPreview) {
        viewModel.loadBook(gitabaseId, bookPreview)
    }

    val uiState by viewModel.uiState.collectAsState()
    val contentsOptions by viewModel.contentsTabOptions.collectAsState()
    val imagesOptionsMap by viewModel.imagesTabOptionsMap.collectAsState()
    val imageFilesExtracted = uiState.imageFilesExtracted
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Calculate total number of tabs (1 for contents + image tabs)
    val totalTabs = 1 + (uiState.bookDetail?.imageTabs?.size ?: 0)
    val pagerState = rememberPagerState(pageCount = { totalTabs })

    // Reset pager to page 0 if current page is out of bounds when tab count changes
    LaunchedEffect(totalTabs, pagerState.currentPage) {
        if (totalTabs > 0 && pagerState.currentPage >= totalTabs) {
            pagerState.animateScrollToPage(0)
        }
    }

    val pagerCurrentPage by remember { derivedStateOf { pagerState.currentPage } }
    val scope = rememberCoroutineScope()

    // Sheet state
    var showSheet by remember { mutableStateOf(false) }

    // Load options for current image tab when sheet opens
    LaunchedEffect(showSheet, pagerCurrentPage, uiState.bookDetail) {
        if (showSheet && pagerCurrentPage > 0) {
            val imageTab = uiState.bookDetail?.imageTabs?.getOrNull(pagerCurrentPage - 1)
            imageTab?.let {
                if (!imagesOptionsMap.containsKey(it.type)) {
                    viewModel.getImageTabOptions(it.type)
                }
            }
        }
    }

    // Calculate text size multiplier: 1.0 + (textSize * 0.2)
    // textSize ranges from -2 to 2, so multiplier ranges from 0.6 to 1.4
    // Default text size is hardcoded, and 20% step is hardcoded (0.2)
    val textSizeMultiplier = 1.0f + (contentsOptions.textSize * 0.2f)

    // Simple sticky header threshold
    val headerHeightPx = LocalDensity.current.run { HEADER_ROW_HEIGHT.toPx() }
    val isRow2Sticky = remember {
        derivedStateOf {
            scrollState.value > headerHeightPx
        }
    }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // available.y < 0 means scrolling UP (user swiping up)
                // available.y > 0 means scrolling DOWN (swiping down)
                val delta = available.y

                return if (delta < 0 && !isRow2Sticky.value) {
                    // Consume scroll by outer scroll until header collapses
                    val old = scrollState.value
                    scrollState.dispatchRawDelta(-delta) // scroll outer Column
                    val consumed = scrollState.value - old
                    Offset(0f, -consumed.toFloat())
                } else {
                    Offset.Zero
                }
            }
        }
    }


    // Tab click handler
    val onTabSelected: (Int) -> Unit = { tabIndex: Int ->
        scope.launch {
            pagerState.animateScrollToPage(tabIndex)
        }
    }

    when {
        uiState.bookDetail != null -> {
            val bookDetail = uiState.bookDetail!!

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Main scrollable content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .nestedScroll(nestedScrollConnection)
                ) {
                    // Row 1: Header with buttons + cover image
                    HeaderRow(
                        bookDetail = bookDetail,
                        onNavigateBack = onNavigateBack,
                        onSearchClick = onSearchClick
                    )

                    // Row 2: Author + title + tabs (becomes sticky when scrolled)
                    AuthorTitleTabsRow(
                        bookDetail = bookDetail,
                        selectedTabIndex = pagerCurrentPage,
                        onTabSelected = onTabSelected,
                        showSheet = showSheet,
                        onShowSheetChange = { showSheet = it },
                    )

                    // Create and remember page data once
                    val pageData = remember(bookDetail) {
                        val contentsData = "contents" to bookDetail
                        val imageData = bookDetail.imageTabs?.mapIndexed { index, imageTab ->
                            "image_$index" to imageTab
                        } ?: emptyList()

                        listOf(contentsData) + imageData
                    }

                    val density = LocalDensity.current
                    val configuration = LocalConfiguration.current

                    // Convert the screen height from dp to pixels
                    val screenHeightPx = with(density) {
                        configuration.screenHeightDp.dp.toPx()
                    }

                    val availableHeightDp = with(density) {
                        (screenHeightPx - headerHeightPx).toDp()
                    }
                    // Content using HorizontalPager
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(availableHeightDp),
                        pageSpacing = 0.dp,
                    ) { page ->
                        if (page < pageData.size) {
                            val (pageType, data) = pageData[page]
                            when (pageType) {
                                "contents" -> {
                                    @Suppress("UNCHECKED_CAST")
                                    TabBookContents(
                                        bookDetail = data as BookDetail,
                                        textSizeMultiplier = textSizeMultiplier,
                                        columns = contentsOptions.columns,
                                        onChapterClick = { chapterNumber ->
                                            onNavigateToChapter(chapterNumber)
                                        }
                                    )
                                }

                                else -> {
                                    @Suppress("UNCHECKED_CAST")
                                    val imageTab = data as BookImageTab
                                    // Get options for this ImageType
                                    val imageType = imageTab.type
                                    val tabOptions = imagesOptionsMap[imageType] ?: BookImagesTabOptions()
                                    // Load options on first access for this ImageType
                                    LaunchedEffect(imageType) {
                                        if (!imagesOptionsMap.containsKey(imageType)) {
                                            viewModel.getImageTabOptions(imageType)
                                        }
                                    }
                                    TabBookImages(
                                        imageTab = imageTab,
                                        columns = tabOptions.columns,
                                        groupByChapters = tabOptions.groupByChapter,
                                        imageFilesExtracted = imageFilesExtracted
                                    )
                                }
                            }
                        }
                    }
                }

                // Sticky header overlay when Row 2 should be sticky
                if (isRow2Sticky.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .zIndex(1f)
                    ) {
                        AuthorTitleTabsRow(
                            bookDetail = bookDetail,
                            selectedTabIndex = pagerCurrentPage,
                            onTabSelected = onTabSelected,
                            showSheet = showSheet,
                            onShowSheetChange = { showSheet = it },
                        )
                    }
                }

                // Tab options sheet
                TabOptionsSheet(
                    showSheet = showSheet,
                    onDismissRequest = { showSheet = false },
                    currentTabIndex = pagerCurrentPage,
                    onTabColumnsChanged = { newColumns ->
                        // Save columns preference based on current tab index
                        if (pagerCurrentPage == 0) {
                            viewModel.setContentsTabOptions(
                                contentsOptions.copy(columns = newColumns)
                            )
                        } else {
                            val imageTab = uiState.bookDetail?.imageTabs?.getOrNull(pagerCurrentPage - 1)
                            imageTab?.let {
                                val currentOptions = imagesOptionsMap[it.type] ?: BookImagesTabOptions()
                                viewModel.setImageTabOptions(
                                    it.type,
                                    currentOptions.copy(columns = newColumns)
                                )
                            }
                        }
                    },
                    onGroupByChaptersChange = { grouped ->
                        // Save groupByChapters preference for current image tab
                        if (pagerCurrentPage > 0) {
                            val imageTab = uiState.bookDetail?.imageTabs?.getOrNull(pagerCurrentPage - 1)
                            imageTab?.let {
                                val currentOptions = imagesOptionsMap[it.type] ?: BookImagesTabOptions()
                                viewModel.setImageTabOptions(
                                    it.type,
                                    currentOptions.copy(groupByChapter = grouped)
                                )
                            }
                        }
                    },
                    onTextSizeChange = { newTextSize ->
                        // Save text size via ViewModel
                        viewModel.setContentsTabOptions(
                            contentsOptions.copy(textSize = newTextSize)
                        )
                    },
                    initialContentsOptions = contentsOptions,
                    initialImagesOptions = if (pagerCurrentPage > 0) {
                        val imageTab = uiState.bookDetail?.imageTabs?.getOrNull(pagerCurrentPage - 1)
                        imageTab?.let { imagesOptionsMap[it.type] }
                    } else {
                        null
                    }
                )
            }
        }

        uiState.isLoading -> LoadingState()
        uiState.error != null -> ErrorState(uiState.error)
        else -> ErrorState("No book data")

    }
}
