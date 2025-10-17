package com.gbr.settings.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gbr.designsystem.components.radiobuttonwithtext.RadioButtonWithTextView
import com.gbr.settings.components.SettingsAppBar
import com.gbr.settings.viewmodel.SettingsViewModel
import com.gbr.settings.viewmodel.ThemeOption

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    BackHandler {
        onNavigateBack()
    }
    
    Scaffold(
        topBar = {
            SettingsAppBar(
                onBackClick = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Application Theme Section
            Text(
                text = "Application theme",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Theme Options
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RadioButtonWithTextView(
                    text = ThemeOption.LIGHT.displayName,
                    isSelected = uiState.selectedTheme == ThemeOption.LIGHT,
                    onSelected = { viewModel.selectTheme(ThemeOption.LIGHT) }
                )
                
                RadioButtonWithTextView(
                    text = ThemeOption.DARK.displayName,
                    isSelected = uiState.selectedTheme == ThemeOption.DARK,
                    onSelected = { viewModel.selectTheme(ThemeOption.DARK) }
                )
                
                RadioButtonWithTextView(
                    text = ThemeOption.SYSTEM.displayName,
                    isSelected = uiState.selectedTheme == ThemeOption.SYSTEM,
                    onSelected = { viewModel.selectTheme(ThemeOption.SYSTEM) }
                )
            }
        }
    }
}




