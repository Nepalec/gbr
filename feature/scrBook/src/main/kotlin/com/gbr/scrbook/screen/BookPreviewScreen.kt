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
import androidx.compose.runtime.key
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
import android.widget.Toast
import androidx.hilt.navigation.compose.hiltViewModel
import com.gbr.model.book.BookDetail
import com.gbr.model.book.BookImageTab
import com.gbr.model.book.BookPreview
import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.gitabase.ImageType
import com.gbr.scrbook.screen.components.AuthorTitleTabsRow
import com.gbr.scrbook.screen.components.TabBookContents
import com.gbr.scrbook.screen.components.ErrorState
import com.gbr.scrbook.screen.components.HEADER_ROW_HEIGHT
import com.gbr.scrbook.screen.components.HeaderRow
import com.gbr.scrbook.screen.components.TabBookImages
import com.gbr.scrbook.screen.components.LoadingState
import com.gbr.scrbook.screen.components.TabOptionsSheet
import com.gbr.scrbook.viewmodel.BookPreviewViewModel

@Composable
fun BookPreviewScreen(
    gitabaseId: GitabaseID,
    bookPreview: BookPreview,
    onNavigateBack: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    viewModel: BookPreviewViewModel = hiltViewModel()
) {
    // Load book data when screen is composed
    LaunchedEffect(gitabaseId, bookPreview) {
        viewModel.loadBook(gitabaseId, bookPreview)
    }

    val uiState by viewModel.uiState.collectAsState()
    val textSize by viewModel.textSize.collectAsState()
    val contentsColumns by viewModel.contentsColumns.collectAsState()
    val imagesColumnsMap by viewModel.imagesColumnsMap.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Calculate total number of tabs (1 for contents + image tabs)
    val totalTabs = 1 + (uiState.bookDetail?.imageTabs?.size ?: 0)
    val pagerState = rememberPagerState(pageCount = { totalTabs })
    val pagerCurrentPage by remember { derivedStateOf { pagerState.currentPage } }
    val scope = rememberCoroutineScope()
    
    // Sheet state
    var showSheet by remember { mutableStateOf(false) }
    
    // Load columns for current image tab when sheet opens
    LaunchedEffect(showSheet, pagerCurrentPage, uiState.bookDetail) {
        if (showSheet && pagerCurrentPage > 0) {
            val imageTab = uiState.bookDetail?.imageTabs?.getOrNull(pagerCurrentPage - 1)
            imageTab?.let {
                val imageTypeValue = it.type.value
                if (!imagesColumnsMap.containsKey(imageTypeValue)) {
                    viewModel.getImageTabColumns(imageTypeValue)
                }
            }
        }
    }
    
    // Calculate text size multiplier: 1.0 + (textSize * 0.2)
    // textSize ranges from -2 to 2, so multiplier ranges from 0.6 to 1.4
    // Default text size is hardcoded, and 20% step is hardcoded (0.2)
    val textSizeMultiplier = 1.0f + (textSize * 0.2f)

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
                                        columns = contentsColumns
                                    )
                                }

                                else -> {
                                    @Suppress("UNCHECKED_CAST")
                                    val imageTab = data as BookImageTab
                                    // Get columns for this ImageType
                                    val imageTypeValue = imageTab.type.value
                                    val tabColumns = imagesColumnsMap[imageTypeValue] ?: 1
                                    // Load columns on first access for this ImageType
                                    LaunchedEffect(imageTypeValue) {
                                        if (!imagesColumnsMap.containsKey(imageTypeValue)) {
                                            viewModel.getImageTabColumns(imageTypeValue)
                                        }
                                    }
                                    TabBookImages(
                                        imageTab = imageTab,
                                        columns = tabColumns
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
                            viewModel.setContentsColumns(newColumns)
                        } else {
                            val imageTab = uiState.bookDetail?.imageTabs?.getOrNull(pagerCurrentPage - 1)
                            imageTab?.let {
                                viewModel.setImagesColumns(it.type.value, newColumns)
                            }
                        }
                        Toast.makeText(context, "Columns: $newColumns", Toast.LENGTH_SHORT).show()
                    },
                    onGroupByChaptersChange = { grouped ->
                        // Handle group by chapters change
                        Toast.makeText(context, "Group by chapters: $grouped", Toast.LENGTH_SHORT).show()
                    },
                    onTextSizeChange = { newTextSize ->
                        // Save text size via ViewModel
                        viewModel.setTextSize(newTextSize)
                        Toast.makeText(context, "Text size: $newTextSize", Toast.LENGTH_SHORT).show()
                    },
                    initialTextSize = textSize,
                    initialContentsColumns = contentsColumns,
                    initialImagesColumns = if (pagerCurrentPage > 0) {
                        val imageTab = uiState.bookDetail?.imageTabs?.getOrNull(pagerCurrentPage - 1)
                        imageTab?.let { imagesColumnsMap[it.type.value] } ?: 1
                    } else {
                        1
                    }
                )
            }
        }

        uiState.isLoading -> LoadingState()
        uiState.error != null -> ErrorState(uiState.error)
        else -> ErrorState("No book data")

    }
}
