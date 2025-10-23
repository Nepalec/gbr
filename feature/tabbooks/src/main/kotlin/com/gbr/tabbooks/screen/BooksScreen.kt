package com.gbr.tabbooks.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.gbr.designsystem.R
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gbr.tabbooks.components.CustomAppBar
import com.gbr.tabbooks.viewmodel.BooksViewModel
import com.gbr.designsystem.components.ShimmerEffect
import com.gbr.model.gitabase.GitabaseType
import kotlinx.coroutines.launch
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.gbr.model.gitabase.Gitabase

@Composable
fun BooksScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToDownloader: () -> Unit = {},
    viewModel: BooksViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // State for file picker
    var showFilePicker by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Header with title and action buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Gitabase Packages",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Download from internet button
                        OutlinedIconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                }
                                onNavigateToDownloader()
                            }
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.download_24px),
                                contentDescription = "Download from internet",
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Add from local storage button
                        OutlinedIconButton(
                            onClick = {
                                showFilePicker = true
                            }
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.folder_24px),
                                contentDescription = "Add from local storage",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Show shimmering effects for copying gitabases
                    items(uiState.copyingGitabases.size) { index ->
                        val copyingGitabase = uiState.copyingGitabases.elementAt(index)
                        ShimmerEffect()
                    }

                    // Show regular gitabases
                    items(uiState.gitabases.size) { index ->
                        val gitabase = uiState.gitabases.elementAt(index)
                        CustomGitabaseDrawerItem(
                            gitabase = gitabase,
                            isSelected = uiState.selectedGitabase?.id == gitabase.id,
                            onSelect = {
                                scope.launch {
                                    drawerState.close()
                                }
                                viewModel.selectGitabase(gitabase)
                            },
                            onDelete = {
                                viewModel.removeGitabase(gitabase)
                            }
                        )
                    }
                }

                // Show message if no gitabases found
                if (uiState.gitabases.isEmpty()) {
                    Text(
                        text = "No Gitabase files found",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CustomAppBar(
                    title = uiState.selectedGitabase?.title ?: "Books",
                    onNavigationClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    onActionClick = onNavigateToSettings
                )
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                if (uiState.error != null) {
                    Text(
                        text = "Error: ${uiState.error}",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error
                    )
                } else if (uiState.selectedGitabase != null) {
                    if (uiState.books.isEmpty()) {
                        // No books in selected gitabase
                        Text(
                            text = "No books found in ${uiState.selectedGitabase?.title}",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        // Display books list
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentPadding = PaddingValues(bottom = 80.dp), // Add bottom padding for navigation bar
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            items(uiState.books.size) { index ->
                                val book = uiState.books[index]
                                BookItem(book = book, onClick = { /* TODO */ })
                            }
                        }
                    }
                } else {
                    Column(
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Found ${uiState.gitabases.size} Gitabase files",
                            style = MaterialTheme.typography.headlineLarge,
                            textAlign = TextAlign.Center,
                            fontSize = 24.sp
                        )
                        Text(
                            text = "Select a Gitabase from the menu to view its books",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
    if (showFilePicker) {
        FilePickerHandler(
            showPicker = showFilePicker,
            hidePicker = { showFilePicker = false },
            onFileSelected = { filePath ->
                viewModel.copyGitabaseFromLocal(filePath)
                showFilePicker = false
            }
        )
    }
}

/**
 * Gets the file path from a URI.
 * Preserves the original filename when possible.
 */
private fun getFilePathFromUri(context: android.content.Context, uri: Uri): String? {
    return try {
        // For file:// URIs, we can get the path directly
        if (uri.scheme == "file") {
            uri.path
        } else {
            // For content:// URIs, we need to copy the file to a temporary location
            // Try to get the original filename from the URI
            val originalFileName = getOriginalFileName(context, uri)
            val tempFile = if (originalFileName != null) {
                java.io.File(context.cacheDir, originalFileName)
            } else {
                java.io.File.createTempFile("gitabase_", ".db", context.cacheDir)
            }

            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            tempFile.absolutePath
        }
    } catch (e: Exception) {
        null
    }
}

/**
 * Attempts to get the original filename from a content URI.
 */
private fun getOriginalFileName(context: android.content.Context, uri: Uri): String? {
    return try {
        // Try to get filename from content resolver
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    it.getString(nameIndex)
                } else null
            } else null
        }
    } catch (e: Exception) {
        null
    }
}

@Composable
private fun BookItem(
    book: com.gbr.model.book.Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        onClick = onClick,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                book.author?.let { author ->
                    Text(
                        text = author,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Text(
                    text = book.title ?: "Untitled",
                    style = MaterialTheme.typography.titleLarge
                )

            }
        }
    }
}

@Composable
fun GitabaseItemLabel(
    title: String,
    subtitle: String,
    showDeleteButton: Boolean = true,
    onDelete: () -> Unit = {},
    isSelected: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(start = 24.dp, end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                maxLines = 3,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = if (isSelected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurface,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected)
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }

        if (showDeleteButton) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.delete_24px),
                    contentDescription = "Delete $title",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun CustomGitabaseDrawerItem(
    gitabase: Gitabase,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .background(
                color = if (isSelected)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surface,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            .clickable { onSelect() }  // Handle click directly on Box
    ) {
        GitabaseItemLabel(
            title = gitabase.title,
            subtitle = "${gitabase.id.type.value}_${gitabase.id.lang.value}",
            showDeleteButton = gitabase.id.type != GitabaseType.HELP && gitabase.id.type != GitabaseType.MY_BOOKS,
            isSelected = isSelected,
            onDelete = onDelete
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GitabaseItemLabelPreview() {
    MaterialTheme {
        Surface {
            GitabaseItemLabel(
                title = "Канонические тексты Гаудия-вайшнавов",
                subtitle = "texts_rus",
                showDeleteButton = true,
                onDelete = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomGitabaseDrawerItemPreview() {
    MaterialTheme {
        Surface {
            CustomGitabaseDrawerItem(
                gitabase = Gitabase(
                    id = com.gbr.model.gitabase.GitabaseID(
                        type = GitabaseType.TEXTS,
                        lang = com.gbr.model.gitabase.GitabaseLang.RUS
                    ),
                    title = "Канонические тексты Гаудия-вайшнавов",
                    version = 1,
                    filePath = "texts_rus",
                    lastModified = "2023-06-01"
                ),
                isSelected = false,
                onSelect = {},
                onDelete = {}
            )
        }
    }
}
