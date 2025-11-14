package com.gbr.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.gbr.common.navigation.SubGraphDest


@Composable
internal fun TabsNavigation(
    selectedTabIndex: Int,
    booksNavController: NavHostController,
    innerPadding: PaddingValues,
    navigator: DefaultNavigator,
    readingNavController: NavHostController,
    discussNavController: NavHostController,
    notesNavController: NavHostController,
    profileNavController: NavHostController
) {
    when (selectedTabIndex) {
        0 -> {
            NavHost(
                navController = booksNavController,
                startDestination = SubGraphDest.Books,
                modifier = Modifier.padding(innerPadding)
            ) {
                navigator.booksFeature.registerGraph(booksNavController, this)
                navigator.downloaderFeature.registerGraph(booksNavController, this)
                navigator.settingsFeature.registerGraph(booksNavController, this)
            }
        }

        1 -> {
            NavHost(
                navController = readingNavController,
                startDestination = SubGraphDest.Reading,
                modifier = Modifier.padding(innerPadding)
            ) {
                navigator.readingFeature.registerGraph(readingNavController, this)
                navigator.settingsFeature.registerGraph(readingNavController, this)
            }
        }

        2 -> {
            NavHost(
                navController = discussNavController,
                startDestination = SubGraphDest.Discuss,
                modifier = Modifier.padding(innerPadding)
            ) {
                navigator.discussFeature.registerGraph(discussNavController, this)
                navigator.settingsFeature.registerGraph(discussNavController, this)
            }
        }

        3 -> {
            NavHost(
                navController = notesNavController,
                startDestination = SubGraphDest.Notes,
                modifier = Modifier.padding(innerPadding)
            ) {
                navigator.notesFeature.registerGraph(notesNavController, this)
                navigator.settingsFeature.registerGraph(notesNavController, this)
            }
        }

        4 -> {
            NavHost(
                navController = profileNavController,
                startDestination = SubGraphDest.Profile,
                modifier = Modifier.padding(innerPadding)
            ) {
                navigator.profileFeature.registerGraph(profileNavController, this)
                navigator.settingsFeature.registerGraph(profileNavController, this)
            }
        }
    }
}