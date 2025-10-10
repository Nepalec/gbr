package com.gbr.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.gbr.designsystem.components.navigationbar.textandicon.NavigationBarWithTextAndIconView

@Composable
fun GbrNavHost() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    
    val destinations = TopLevelDestination.values()
    val items = destinations.map { it.title }
    val icons = destinations.map { it.icon }
    val selectedIcons = destinations.map { it.selectedIcon }

    Scaffold(
        bottomBar = {
            NavigationBarWithTextAndIconView(
                items = items,
                icons = icons,
                selectedIcons = selectedIcons,
                selectedIndex = selectedTabIndex,
                onItemClick = { index ->
                    selectedTabIndex = index
                }
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
                text = destinations[selectedTabIndex].title,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                fontSize = 24.sp
            )
        }
    }
}
