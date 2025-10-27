package com.gbr.scrbook.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import com.gbr.model.book.BookDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorTitleTabsRow(
    bookDetail: BookDetail,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }

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
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.weight(1f), // takes remaining space
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                edgePadding = 0.dp,
                divider = {}
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { onTabSelected(0) },
                    text = { Text("Contents", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface) },
                    modifier = Modifier.wrapContentWidth(),

                )
                bookDetail.imageTabs?.forEachIndexed { index, imageTab ->
                    Tab(
                        selected = selectedTabIndex == index + 1,
                        onClick = { onTabSelected(index + 1) },
                        text = { Text(imageTab.tabTitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface) },
                    )
                }
            }

            IconButton(
             //   modifier = Modifier.background(color = Color.Blue),
                onClick = {
                    if (showSheet) {
                        scope.launch { sheetState.hide() }.invokeOnCompletion { showSheet = false }
                    } else {
                        showSheet = true
                    }
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

    if (showSheet) {
        BackHandler {
            scope.launch { sheetState.hide() }.invokeOnCompletion { showSheet = false }
        }
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            // Sheet content placeholder
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "More actions", style = MaterialTheme.typography.titleMedium)
                Text(text = "Coming soon", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
