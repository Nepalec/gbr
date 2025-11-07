package com.gbr.scrchapter.screen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.gbr.designsystem.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterTitleBar(
    bookTitle: String,
    chapterTitle: String,
    onDrawerClick: () -> Unit,
    onMoreOptionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = bookTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        lineHeight = MaterialTheme.typography.titleMedium.fontSize * 1.0f
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = chapterTitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = MaterialTheme.typography.bodyMedium.fontSize * 1.0f
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onDrawerClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.menu_24px),
                    contentDescription = "Open drawer"
                )
            }
        },
        actions = {
            IconButton(onClick = onMoreOptionsClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.more_vert_24px),
                    contentDescription = "More options"
                )
            }
        },
        colors = TopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            scrolledContainerColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
            subtitleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        windowInsets = WindowInsets(0),
        modifier = modifier
    )
}

