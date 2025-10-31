package com.gbr.scrbook.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

@Composable
fun TabBookImages(imageTab: BookImageTab) {
    Box(modifier = Modifier.fillMaxSize()
       // .background(Color(Random.nextLong() or 0xFF000000))
        .padding(vertical = 8.dp)){
        LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp))
        {
            items(imageTab.images) { imageItem ->
                ImagePlaceholder(
                    imageItem = imageItem,
                    modifier = Modifier.fillMaxWidth()
                )
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
