package com.gbr.scrchapter.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gbr.model.book.BookDetail
import com.gbr.model.book.ChapterContentsItem
import com.gbr.model.book.TextItem
import com.gbr.model.gitabase.ImageType

@Composable
fun ChapterContents(
    isLoading: Boolean,
    error: String?,
    chapter: ChapterContentsItem?,
    bookDetail: BookDetail?,
    chapterTexts: List<TextItem>,
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

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
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
                          ChapterTextItem(textItem)
                        }
                    }
                }
            }
        }
    }
}



