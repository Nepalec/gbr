package com.gbr.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.gbr.common.navigation.SubGraphDest
import com.gbr.designsystem.components.navigationbar.textandicon.NavigationBarWithTextAndIconView
import com.gbr.navigation.DefaultNavigator

@Composable
fun GbrNavHost(
    defaultNavigator: DefaultNavigator
) {
    val navController = rememberNavController()
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
                    when (index) {
                        0 -> navController.navigate(SubGraphDest.Books)
                        1 -> navController.navigate(SubGraphDest.Reading)
                        2 -> navController.navigate(SubGraphDest.Notes)
                        3 -> navController.navigate(SubGraphDest.Profile)
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    ) { innerPadding ->
        androidx.navigation.compose.NavHost(
            navController = navController,
            startDestination = SubGraphDest.Books,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Register all feature navigation graphs
            defaultNavigator.booksFeature.registerGraph(navController, this)
            defaultNavigator.downloaderFeature.registerGraph(navController, this)
            defaultNavigator.readingFeature.registerGraph(navController, this)
            defaultNavigator.notesFeature.registerGraph(navController, this)
            defaultNavigator.profileFeature.registerGraph(navController, this)
            defaultNavigator.settingsFeature.registerGraph(navController, this)
        }
    }
}
