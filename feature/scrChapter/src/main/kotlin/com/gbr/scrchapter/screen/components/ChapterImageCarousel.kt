package com.gbr.scrchapter.screen.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gbr.model.book.ImageFileItem
import java.io.File

@Composable
fun ChapterImageCarousel(
    images: List<ImageFileItem>,
    modifier: Modifier = Modifier
) {
    if (images.isEmpty()) {
        return
    }

    val context = LocalContext.current
    val appInternalFolder = context.filesDir

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(images) { imageItem ->
            val imageFile = imageItem.fullImagePath?.let { path ->
                File(appInternalFolder, path)
            }

            if (imageFile != null && imageFile.exists()) {
                Card(
                    modifier = Modifier
                        .width(200.dp)
                        .height(200.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    AsyncImage(
                        model = imageFile,
                        contentDescription = imageItem.imageDescription ?: imageItem.id,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

