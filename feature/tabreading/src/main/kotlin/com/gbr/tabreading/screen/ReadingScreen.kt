package com.gbr.tabreading.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gbr.tabreading.R
import com.gbr.tabreading.components.CustomAppBar
import com.gbr.tabreading.viewmodel.ReadingViewModel

@Composable
fun ReadingScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    viewModel: ReadingViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            CustomAppBar(
                title = stringResource(R.string.reading),
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
            Text(
                text = stringResource(R.string.reading_content),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                fontSize = 24.sp
            )
        }
    }
}
