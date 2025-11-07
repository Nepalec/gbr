package com.gbr.scrbook.screen.components

import android.text.Html
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gbr.model.book.ChapterContentsItem

@Composable
fun ChapterItem(
    chapter: ChapterContentsItem,
    textSizeMultiplier: Float = 1f,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Chapter ${chapter.number}",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = MaterialTheme.typography.bodySmall.fontSize * textSizeMultiplier
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = Html.fromHtml(chapter.title, Html.FROM_HTML_MODE_COMPACT).toString(),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = MaterialTheme.typography.titleMedium.fontSize * textSizeMultiplier
                ),
                fontWeight = FontWeight.Bold
            )
            val intro = chapter.intro
            if (!intro.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = intro,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize * textSizeMultiplier
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
