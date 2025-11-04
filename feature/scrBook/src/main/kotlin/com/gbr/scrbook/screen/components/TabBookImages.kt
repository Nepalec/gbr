package com.gbr.scrbook.screen.components

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.key
import coil.compose.AsyncImagePainter
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.gbr.model.book.BookImageTab
import java.io.File
import kotlin.random.Random

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun TabBookImages(
    imageTab: BookImageTab,
    columns: Int = 1,
    groupByChapters: Boolean = false,
    imageFilesExtracted: Boolean = false
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
                                verticalArrangement = Arrangement.spacedBy(if (columns == 1) 8.dp else 1.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items.forEach { imageItem ->
                                    ImagePlaceholder(
                                        imageItem = imageItem,
                                        modifier = Modifier.width(itemWidth),
                                        columns = columns,
                                        imageFilesExtracted = imageFilesExtracted
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
                modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
                verticalArrangement = if (columns == 1) Arrangement.spacedBy(8.dp) else Arrangement.spacedBy(1.dp)
            ) {
                items(imageTab.images) { imageItem ->
                    ImagePlaceholder(
                        imageItem = imageItem,
                        modifier = Modifier.fillMaxWidth(),
                        columns = columns,
                        imageFilesExtracted = imageFilesExtracted
                    )
                }
            }
        }
    }

}

@Composable
private fun ImagePlaceholder(
    imageItem: com.gbr.model.book.ImageFileItem,
    modifier: Modifier = Modifier,
    columns: Int = 1,
    imageFilesExtracted: Boolean = false
) {
    val context = LocalContext.current

    Column(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(2.dp),
            shape = RectangleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            if (imageFilesExtracted && !imageItem.fullImagePath.isNullOrEmpty()) {
                // Show actual image using Coil with error tracking
                val imageFile = File(context.filesDir, imageItem.fullImagePath)
                var imageError by remember { mutableStateOf<String?>(null) }
                var imageState by remember { mutableStateOf<AsyncImagePainter.State?>(null) }

                // Pre-check file before attempting to load
                val fileCheckError = when {
                    !imageFile.exists() -> "File not found: ${imageFile.absolutePath}"
                    imageFile.length() == 0L -> "File is empty (0 bytes)"
                    !imageFile.canRead() -> "File is not readable"
                    else -> null
                }

                if (fileCheckError != null) {
                    imageError = fileCheckError
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = imageFile,
                        contentDescription = imageItem.imageDescription ?: imageItem.id,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        onState = { state ->
                            imageState = state
                            when (state) {
                                is AsyncImagePainter.State.Error -> {
                                    val error = state.result.throwable
                                    imageError = when {
                                        error.message?.contains("decode", ignoreCase = true) == true ->
                                            "Decode error: ${error.message}"
                                        error.message?.contains("bytecode", ignoreCase = true) == true ->
                                            "Bytecode error: ${error.message}"
                                        error.message?.contains("corrupt", ignoreCase = true) == true ->
                                            "Corrupted file: ${error.message}"
                                        error.message?.contains("format", ignoreCase = true) == true ->
                                            "Invalid format: ${error.message}"
                                        else ->
                                            "Load error: ${error.message ?: error.javaClass.simpleName}"
                                    }
                                }
                                is AsyncImagePainter.State.Success -> {
                                    imageError = null
                                }
                                else -> {}
                            }
                        }
                    )

                    // Show error overlay if there's an error
                    if (imageError != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text(
                                    text = "⚠️",
                                    fontSize = 24.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = imageError!!,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    textAlign = TextAlign.Center,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            } else {
                // Show placeholder
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

        // Show image description below the placeholder when columns == 1
        val imageDescription = imageItem.imageDescription
        if (columns == 1 && !imageDescription.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = imageDescription,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
