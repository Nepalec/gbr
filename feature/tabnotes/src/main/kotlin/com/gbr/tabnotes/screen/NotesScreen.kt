package com.gbr.tabnotes.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gbr.tabnotes.R
import com.gbr.tabnotes.components.CustomAppBar
import com.gbr.tabnotes.viewmodel.NotesViewModel
import components.buttons.FilledButtonView

@Composable
fun NotesScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    viewModel: NotesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.importNotes(it.toString())
        }
    }

    Scaffold(
        topBar = {
            CustomAppBar(
                title = stringResource(R.string.notes),
                onNavigationClick = onNavigateBack,
                onActionClick = onNavigateToSettings
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
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }
                uiState.isImporting -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Importing notes...",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                uiState.error != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Error: ${uiState.error}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        FilledButtonView(
                            text = "Try Again",
                            onClick = { filePickerLauncher.launch(arrayOf("application/x-sqlite3", "application/octet-stream")) }
                        )
                    }
                }
                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        if (uiState.notes.isEmpty() && uiState.readings.isEmpty() && uiState.tags.isEmpty()) {
                            Text(
                                text = stringResource(R.string.notes_content),
                                style = MaterialTheme.typography.headlineLarge,
                                textAlign = TextAlign.Center,
                                fontSize = 24.sp
                            )
                            Text(
                                text = "Import notes from a SQLite database file",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            Text(
                                text = "Notes: ${uiState.notes.size}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Readings: ${uiState.readings.size}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Tags: ${uiState.tags.size}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        FilledButtonView(
                            text = "Import from .db file",
                            onClick = { filePickerLauncher.launch(arrayOf("application/x-sqlite3", "application/octet-stream")) }
                        )
                    }
                }
            }
        }
    }
}
