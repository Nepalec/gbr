package com.gbr.tabnotes.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gbr.designsystem.R
import com.gbr.model.notes.NotesStorageMode
import com.gbr.tabnotes.viewmodel.NotesViewModel
import com.gbr.tabnotes.R as NotesR

@Composable
fun NotesScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    viewModel: NotesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Determine title based on storage mode
    val title = when (uiState.notesStorageMode) {
        NotesStorageMode.LOCAL -> stringResource(NotesR.string.local_notes)
        NotesStorageMode.CLOUD -> stringResource(NotesR.string.cloud_notes)
    }

    // State for overflow menu visibility
    var showMenu by remember { mutableStateOf(false) }

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.importNotes(it.toString())
        }
    }

    // Handle login requirement
    LaunchedEffect(uiState.importError) {
        if (uiState.importError == "LOGIN_REQUIRED") {
            onNavigateToLogin()
        }
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = { }, // Remove hamburger icon for Notes tab
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.more_vert_24px),
                                contentDescription = stringResource(NotesR.string.cd_more_options)
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            // Show "Import from backup" only if:
                            // - Storage mode is LOCAL, OR
                            // - Storage mode is CLOUD and user is logged in
                            val showImport = uiState.notesStorageMode == NotesStorageMode.LOCAL ||
                                    (uiState.notesStorageMode == NotesStorageMode.CLOUD && uiState.isLoggedIn)

                            if (showImport) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(NotesR.string.import_from_backup)) },
                                    onClick = {
                                        showMenu = false
                                        filePickerLauncher.launch(arrayOf("application/x-sqlite3", "application/octet-stream"))
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text(stringResource(NotesR.string.settings)) },
                                onClick = {
                                    showMenu = false
                                    onNavigateToSettings()
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    scrolledContainerColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                windowInsets = WindowInsets(0),
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading || uiState.isImporting -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        if (uiState.isImporting) {
                        Text(
                                text = stringResource(NotesR.string.importing_notes),
                                style = MaterialTheme.typography.bodyMedium
                        )
                        }
                    }
                }
                uiState.notesStorageMode == NotesStorageMode.CLOUD && !uiState.isLoggedIn -> {
                    // Show login button only when in cloud mode and not logged in
                    Button(
                        onClick = onNavigateToLogin,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = stringResource(NotesR.string.login))
                    }
                }
                else -> {
                    // Always show 2 lines: Notes count and Tags count
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                            Text(
                                text = "Notes: ${uiState.notes.size}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                            text = "Tags: ${uiState.tags.size}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        // Show error message if import failed
                        uiState.importError?.takeIf { it != "LOGIN_REQUIRED" }?.let { error ->
                            Text(
                                text = stringResource(NotesR.string.import_failed, error),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}
