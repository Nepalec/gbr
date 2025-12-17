package com.gbr.settings.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gbr.designsystem.components.radiobuttonwithtext.RadioButtonWithTextView
import com.gbr.settings.R
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
                text = stringResource(R.string.application_theme),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Theme Options
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RadioButtonWithTextView(
                    text = stringResource(R.string.theme_light),
                    isSelected = uiState.selectedTheme == ThemeOption.LIGHT,
                    onSelected = { viewModel.selectTheme(ThemeOption.LIGHT) }
                )

                RadioButtonWithTextView(
                    text = stringResource(R.string.theme_dark),
                    isSelected = uiState.selectedTheme == ThemeOption.DARK,
                    onSelected = { viewModel.selectTheme(ThemeOption.DARK) }
                )

                RadioButtonWithTextView(
                    text = stringResource(R.string.theme_system),
                    isSelected = uiState.selectedTheme == ThemeOption.SYSTEM,
                    onSelected = { viewModel.selectTheme(ThemeOption.SYSTEM) }
                )
            }

            // Logout Button (only show if user is logged in)
            if (uiState.isLoggedIn) {
                Button(
                    onClick = { viewModel.logout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(text = stringResource(R.string.logout))
                }
            }
        }
    }
}




