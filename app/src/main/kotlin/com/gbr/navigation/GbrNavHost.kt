package com.gbr.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.gbr.designsystem.components.navigationbar.textandicon.NavigationBarWithTextAndIconView
import com.gbr.tabbooks.navigation.BooksNavigation
import com.gbr.tabreading.navigation.ReadingNavigation
import com.gbr.tabnotes.navigation.NotesNavigation
import com.gbr.tabprofile.navigation.ProfileNavigation
import com.gbr.settings.navigation.SettingsNavigation

@Composable
fun GbrNavHost() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showSettings by remember { mutableStateOf(false) }
    
    val destinations = TopLevelDestination.values()
    val items = destinations.map { it.title }
    val icons = destinations.map { it.icon }
    val selectedIcons = destinations.map { it.selectedIcon }

    Scaffold(
        bottomBar = {
            if (!showSettings) {
                NavigationBarWithTextAndIconView(
                    items = items,
                    icons = icons,
                    selectedIcons = selectedIcons,
                    selectedIndex = selectedTabIndex,
                    onItemClick = { index ->
                        selectedTabIndex = index
                    }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    ) { innerPadding ->
        if (showSettings) {
            SettingsNavigation(
                onNavigateBack = { showSettings = false }
            )
        } else {
            when (selectedTabIndex) {
                0 -> BooksNavigation(
                    onNavigateBack = { /* Handle back navigation */ },
                    onNavigateToSettings = { showSettings = true }
                )
                1 -> ReadingNavigation(
                    onNavigateBack = { /* Handle back navigation */ },
                    onNavigateToSettings = { showSettings = true }
                )
                2 -> NotesNavigation(
                    onNavigateBack = { /* Handle back navigation */ },
                    onNavigateToSettings = { showSettings = true }
                )
                3 -> ProfileNavigation(
                    onNavigateBack = { /* Handle back navigation */ },
                    onNavigateToSettings = { showSettings = true }
                )
            }
        }
    }
}
