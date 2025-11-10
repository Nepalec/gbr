package com.gbr.scrDownloader.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gbr.designsystem.R
import com.gbr.model.gitabase.Gitabase
import com.gbr.model.gitabase.GitabaseLang
import components.buttons.FilledButtonView
import components.buttons.OutlinedButtonView
import com.gbr.scrDownloader.R as DownloaderR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloaderScreen(
    onNavigateBack: () -> Unit,
    viewModel: DownloaderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(DownloaderR.string.download_gitabase_pack),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.arrow_back_24px),
                            contentDescription = stringResource(DownloaderR.string.cd_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                windowInsets = WindowInsets(0),
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else if (uiState.error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = stringResource(DownloaderR.string.error),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = uiState.error.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // First row: Language selector
                    LanguageSelector(
                        languages = uiState.languages,
                        selectedLanguage = uiState.selectedLanguage,
                        onLanguageSelected = viewModel::selectLanguage
                    )

                    // Second row: Gitabase list
                    GitabaseList(
                        gitabases = uiState.gitabases.filter {
                            it.id.lang == uiState.selectedLanguage
                        },
                        downloadingGitabase = uiState.downloadingGitabase,
                        downloadedGitabases = uiState.downloadedGitabases,
                        onDownloadClick = viewModel::downloadGitabase
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageSelector(
    languages: List<GitabaseLang>,
    selectedLanguage: GitabaseLang?,
    onLanguageSelected: (GitabaseLang) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(DownloaderR.string.select_language),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(languages) { language ->
                LanguageChip(
                    language = language,
                    isSelected = language == selectedLanguage,
                    onClick = { onLanguageSelected(language) }
                )
            }
        }
    }
}

@Composable
private fun LanguageChip(
    language: GitabaseLang,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    if (isSelected) {
        // Use FilledButtonView for selected state
        FilledButtonView(
            text = language.value.uppercase(),
            onClick = onClick,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    } else {
        // Use OutlinedButtonView for unselected state
        OutlinedButtonView(
            text = language.value.uppercase(),
            onClick = onClick,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

@Composable
private fun GitabaseList(
    gitabases: List<Gitabase>,
    downloadingGitabase: Gitabase?,
    downloadedGitabases: Set<Gitabase>,
    onDownloadClick: (Gitabase) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(DownloaderR.string.available_gitabases),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(gitabases) { gitabase ->
                GitabaseItem(
                    gitabase = gitabase,
                    isDownloading = gitabase == downloadingGitabase,
                    isDownloaded = gitabase in downloadedGitabases,
                    onDownloadClick = { onDownloadClick(gitabase) }
                )
            }
        }
    }
}

@Composable
private fun GitabaseItem(
    gitabase: Gitabase,
    isDownloading: Boolean,
    isDownloaded: Boolean,
    onDownloadClick: () -> Unit
) {
    Card(
        onClick = { onDownloadClick() },
        modifier = Modifier
            .fillMaxWidth()
            .background(
                when {
                    isDownloaded -> MaterialTheme.colorScheme.primaryContainer
                    isDownloading -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.surface
                },
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = gitabase.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isDownloaded -> MaterialTheme.colorScheme.onPrimaryContainer
                        isDownloading -> MaterialTheme.colorScheme.onSecondaryContainer
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
                Text(
                    text = gitabase.id.type.value,
                    style = MaterialTheme.typography.bodySmall,
                    color = when {
                        isDownloaded -> MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        isDownloading -> MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            if (isDownloading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            } else if (isDownloaded) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.check_24px),
                    contentDescription = stringResource(DownloaderR.string.cd_downloaded),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
