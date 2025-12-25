package com.gbr.scrchapter.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gbr.model.book.BookDetail
import com.gbr.model.book.ChapterContentsItem
import com.gbr.model.book.TextPreviewItem
import com.gbr.model.gitabase.ImageType
import com.gbr.scrchapter.screen.ScrollPosition

@Composable
fun ChapterContents(
    isLoading: Boolean,
    error: String?,
    chapter: ChapterContentsItem?,
    bookDetail: BookDetail?,
    chapterTexts: List<TextPreviewItem>,
    chapterNumber: Int,
    savedScrollPosition: ScrollPosition?,
    onListStateReady: (LazyListState) -> Unit,
    onTextClick: (TextPreviewItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            chapter != null && bookDetail != null -> {
                // Get PICTURE images from imageTabs
                val pictureImages = bookDetail.imageTabs
                    ?.find { it.type == ImageType.PICTURE }
                    ?.images
                    ?.filter { it.chapterNumber == chapter.number }
                    ?: emptyList()

                val listState = rememberLazyListState(
                    initialFirstVisibleItemIndex = savedScrollPosition?.firstVisibleItemIndex ?: 0,
                    initialFirstVisibleItemScrollOffset = savedScrollPosition?.firstVisibleItemScrollOffset ?: 0
                )

                // Expose listState to parent
                LaunchedEffect(listState) {
                    onListStateReady(listState)
                }

                // Restore scroll position when chapter loads
                LaunchedEffect(chapterNumber, chapterTexts.isNotEmpty()) {
                    savedScrollPosition?.let { position ->
                        if (listState.firstVisibleItemIndex != position.firstVisibleItemIndex ||
                            listState.firstVisibleItemScrollOffset != position.firstVisibleItemScrollOffset
                        ) {
                            listState.scrollToItem(
                                index = position.firstVisibleItemIndex,
                                scrollOffset = position.firstVisibleItemScrollOffset
                            )
                        }
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Image carousel at the top
                    if (pictureImages.isNotEmpty()) {
                        item {
                            ChapterImageCarousel(
                                images = pictureImages,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Chapter texts - display as individual items
                    if (chapterTexts.isNotEmpty()) {
                        items(chapterTexts) { textItem ->
                            ChapterTextItem(
                                textItem = textItem,
                                onClick = { onTextClick(textItem) }
                            )
                        }
                    }
                }
            }
        }
    }
}
