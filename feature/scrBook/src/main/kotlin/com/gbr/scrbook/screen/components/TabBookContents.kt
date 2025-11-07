package com.gbr.scrbook.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gbr.model.book.BookDetail
import kotlin.random.Random

@Composable
fun TabBookContents(
    bookDetail: BookDetail,
    textSizeMultiplier: Float = 1f,
    columns: Int = 1,
    onChapterClick: (Int) -> Unit = {} // chapterNumber
) {
    val chapters = bookDetail.chapters
    val texts = bookDetail.texts
    val listState = rememberLazyListState()

    if (columns == 1) {
        // Use LazyColumn for single column layout
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
              //  .background(Color(Random.nextLong() or 0xFF000000)),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            when {
                chapters != null -> {
                    items(chapters.size) { chapter ->
                        ChapterItem(
                            chapter = chapters[chapter],
                            textSizeMultiplier = textSizeMultiplier,
                            onClick = { onChapterClick(chapters[chapter].number) }
                        )
                    }
                }
                texts != null -> {
                    items(texts.size) { text ->
                        TextItem(
                            text = texts[text],
                            textSizeMultiplier = textSizeMultiplier
                        )
                    }
                }
                else -> {
                    item {
                        Text(
                            text = "No contents available",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    } else {
        // Use LazyVerticalGrid for multi-column layout
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            when {
                chapters != null -> {
                    items(chapters) { chapter ->
                        ChapterItem(
                            chapter = chapter,
                            textSizeMultiplier = textSizeMultiplier,
                            onClick = { onChapterClick(chapter.number) }
                        )
                    }
                }
                texts != null -> {
                    items(texts) { text ->
                        TextItem(
                            text = text,
                            textSizeMultiplier = textSizeMultiplier
                        )
                    }
                }
                else -> {
                    item(span = { GridItemSpan(columns) }) {
                        Text(
                            text = "No contents available",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

