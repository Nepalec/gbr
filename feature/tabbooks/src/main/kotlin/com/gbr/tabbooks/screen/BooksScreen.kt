package com.gbr.tabbooks.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gbr.tabbooks.components.CustomAppBar
import com.gbr.tabbooks.viewmodel.BooksViewModel
import com.gbr.model.gitabase.GitabaseType
import kotlinx.coroutines.launch

@Composable
fun BooksScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
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
                        text = "Gitabase Files",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Download from internet button
                        OutlinedIconButton(
                            onClick = {
                                // TODO: Implement download from internet functionality
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
                
                    // Display gitabase titles as drawer items
                    uiState.gitabases.forEach { gitabase ->
                        val canDelete = gitabase.id.type != GitabaseType.HELP && gitabase.id.type != GitabaseType.MY_BOOKS
                        
                        NavigationDrawerItem(
                            label = { 
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = gitabase.title,
                                        maxLines = 2,
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    if (canDelete) {
                                        IconButton(
                                            onClick = {
                                                viewModel.removeGitabase(gitabase)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = ImageVector.vectorResource(R.drawable.delete_24px),
                                                contentDescription = "Delete ${gitabase.title}",
                                                modifier = Modifier.size(20.dp),
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            },
                            selected = uiState.selectedGitabase?.id == gitabase.id,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                }
                                viewModel.selectGitabase(gitabase)
                            }
                        )
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
                    title = "Books",
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
                    val selectedGitabase = uiState.selectedGitabase
                    Column(
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Selected Gitabase",
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = selectedGitabase?.title ?: "Unknown",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Type: ${selectedGitabase?.id?.type?.value ?: "Unknown"} | Language: ${selectedGitabase?.id?.lang?.value ?: "Unknown"}",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
    
    // Handle file picker
    if (showFilePicker) {
        FilePickerHandler(
            onFileSelected = { filePath ->
                viewModel.copyGitabaseFromLocal(filePath)
                showFilePicker = false
            }
        )
    }
}
