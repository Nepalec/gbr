package com.gbr.scrbook.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import com.gbr.model.book.BookDetail

@Composable
fun AuthorTitleTabsRow(
    bookDetail: BookDetail,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    showSheet: Boolean,
    onShowSheetChange: (Boolean) -> Unit,
) {
    // Calculate total number of tabs (1 for contents + image tabs)
    val totalTabs = 1 + (bookDetail.imageTabs?.size ?: 0)
    // Clamp selectedTabIndex to valid range to prevent IndexOutOfBoundsException
    val clampedSelectedIndex = selectedTabIndex.coerceIn(0, maxOf(0, totalTabs - 1))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Author
        Text(
            text = bookDetail.book.author,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        // Title
        Text(
            text = bookDetail.book.title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScrollableTabRow(
                selectedTabIndex = clampedSelectedIndex,
                modifier = Modifier.weight(1f), // takes remaining space
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                edgePadding = 0.dp,
                divider = {}
            ) {
                BookTab("Contents", 0, clampedSelectedIndex, onTabSelected)

                bookDetail.imageTabs?.forEachIndexed { index, imageTab ->
                    BookTab(imageTab.tabTitle, index + 1, clampedSelectedIndex, onTabSelected)
                }

            }

            IconButton(
                onClick = {
                    onShowSheetChange(!showSheet)
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun BookTab(
    title: String,
    index: Int,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
        Tab(
            selected = selectedTabIndex == index,
            onClick = { onTabSelected(index) },
            text = { Text(title, fontSize = 12.sp) },
            modifier = Modifier
                .wrapContentWidth()
                //.background(Color(Random.nextLong() or 0xFF000000)),

            )

}

