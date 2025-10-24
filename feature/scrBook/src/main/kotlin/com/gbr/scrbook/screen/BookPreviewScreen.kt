package com.gbr.scrbook.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gbr.scrbook.components.BookPreviewAppBar
import com.gbr.scrbook.viewmodel.BookPreviewViewModel

@Composable
fun BookPreviewScreen(
    gitabaseId: com.gbr.model.gitabase.GitabaseID,
    bookId: Int,
    onNavigateBack: () -> Unit = {},
    viewModel: BookPreviewViewModel = hiltViewModel()
) {
    // Load book data when screen is composed
    LaunchedEffect(gitabaseId, bookId) {
        viewModel.loadBook(gitabaseId, bookId)
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            BookPreviewAppBar(
                title = "Book Preview",
                onNavigateBack = onNavigateBack
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
                    Text(
                        text = "Loading...",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )
                }
                uiState.error != null -> {
                    Text(
                        text = "Error: ${uiState.error}",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                uiState.book != null -> {
                    val book = uiState.book!!
                    Text(
                        text = "Book ID: ${book.id}",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp
                    )
                }
                else -> {
                    Text(
                        text = "No book data",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
