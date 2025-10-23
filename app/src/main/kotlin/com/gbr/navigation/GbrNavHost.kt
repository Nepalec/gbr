package com.gbr.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.gbr.common.navigation.SubGraphDest
import com.gbr.designsystem.components.navigationbar.textandicon.NavigationBarWithTextAndIconView

@Composable
fun GbrNavHost(
    defaultNavigator: DefaultNavigator
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Create separate NavControllers for each tab to ensure isolation
    val booksNavController = rememberNavController()
    val readingNavController = rememberNavController()
    val discussNavController = rememberNavController()
    val notesNavController = rememberNavController()
    val profileNavController = rememberNavController()

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

        when (selectedTabIndex) {
            0 -> {
                NavHost(
                    navController = booksNavController,
                    startDestination = SubGraphDest.Books,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    defaultNavigator.booksFeature.registerGraph(booksNavController, this)
                    defaultNavigator.downloaderFeature.registerGraph(booksNavController, this)
                    defaultNavigator.settingsFeature.registerGraph(booksNavController, this)
                }
            }
            1 -> {
                NavHost(
                    navController = readingNavController,
                    startDestination = SubGraphDest.Reading,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    defaultNavigator.readingFeature.registerGraph(readingNavController, this)
                    defaultNavigator.settingsFeature.registerGraph(readingNavController, this)
                }
            }
            2 -> {
                NavHost(
                    navController = discussNavController,
                    startDestination = SubGraphDest.Discuss,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    defaultNavigator.discussFeature.registerGraph(discussNavController, this)
                    defaultNavigator.settingsFeature.registerGraph(discussNavController, this)
                }
            }
            3 -> {
                NavHost(
                    navController = notesNavController,
                    startDestination = SubGraphDest.Notes,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    defaultNavigator.notesFeature.registerGraph(notesNavController, this)
                    defaultNavigator.settingsFeature.registerGraph(notesNavController, this)
                }
            }
            4 -> {
                NavHost(
                    navController = profileNavController,
                    startDestination = SubGraphDest.Profile,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    defaultNavigator.profileFeature.registerGraph(profileNavController, this)
                    defaultNavigator.settingsFeature.registerGraph(profileNavController, this)
                }
            }
        }
    }
}
