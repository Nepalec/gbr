package com.gbr.scrbook.screen.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gbr.model.book.BookImageTab
import kotlin.random.Random

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun TabBookImages(
    imageTab: BookImageTab,
    columns: Int = 1,
    groupByChapters: Boolean = false
) {
    Box(modifier = Modifier.fillMaxSize()
       // .background(Color(Random.nextLong() or 0xFF000000))
        .padding(vertical = 8.dp)){
        if (groupByChapters) {
            // Grouped view with sticky headers
            val groupedImages = imageTab.images.groupBy {
                (it.chapterNumber ?: 0) to (it.chapterTitle ?: "No Chapter")
            }.toSortedMap { a, b ->
                a.first.compareTo(b.first) // Sort by chapter number
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                groupedImages.forEach { (chapterKey, items) ->
                    val (chapterNumber, chapterTitle) = chapterKey

                    // Create chapter title string
                    val headerText = when {
                        !chapterTitle.isNullOrEmpty() -> "$chapterNumber. $chapterTitle"
                        chapterNumber > 0 -> "Chapter $chapterNumber"
                        else -> "No Chapter"
                    }

                    stickyHeader {
                        Text(
                            text = headerText,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(8.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    item {
                        BoxWithConstraints(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val spacingDp = (columns - 1) * 2f
                            val itemWidth = ((maxWidth.value - spacingDp) / columns).dp
                            FlowRow(
                                maxItemsInEachRow = columns,
                                horizontalArrangement = Arrangement.spacedBy(1.dp),
                                verticalArrangement = Arrangement.spacedBy(1.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items.forEach { imageItem ->
                                    ImagePlaceholder(
                                        imageItem = imageItem,
                                        modifier = Modifier.width(itemWidth)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Ungrouped view - original implementation
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp)
            ) {
                items(imageTab.images) { imageItem ->
                    ImagePlaceholder(
                        imageItem = imageItem,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

}

@Composable
private fun ImagePlaceholder(
    imageItem: com.gbr.model.book.ImageFileItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp).padding(2.dp),
        shape = RectangleShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🖼️", // Image placeholder emoji
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = imageItem.id,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = imageItem.format.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
