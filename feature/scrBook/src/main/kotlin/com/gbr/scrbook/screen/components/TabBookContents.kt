package com.gbr.scrbook.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
fun TabBookContents(bookDetail: BookDetail) {
    val chapters = bookDetail.chapters
    val texts = bookDetail.texts
    val listState = rememberLazyListState()

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
                    ChapterItem(chapter = chapters[chapter])
                }
            }
            texts != null -> {
                items(texts.size) { text ->
                    TextItem(text = texts[text])
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
}

