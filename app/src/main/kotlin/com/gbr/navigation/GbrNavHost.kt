package com.gbr.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gbr.common.navigation.FullscreenDest
import com.gbr.designsystem.components.navigationbar.textandicon.NavigationBarWithTextAndIconView

@Composable
internal fun GbrNavHost(
    defaultNavigator: DefaultNavigator,
    snackbarHostState: SnackbarHostState
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Create separate NavControllers for each tab to ensure isolation
    val booksNavController = rememberNavController()
    val readingNavController = rememberNavController()
    val discussNavController = rememberNavController()
    val notesNavController = rememberNavController()
    val profileNavController = rememberNavController()

    // Fullscreen NavController for content viewing
    val fullscreenNavController = rememberNavController()

    // Track if fullscreen content is active
    // Only consider fullscreen if we're NOT on placeholder Dest.Books
    // Since fullscreenNavController only has Dest.Books (placeholder) and FullscreenDest.* destinations,
    // checking that we're not on Dest.Books means we're on a FullscreenDest
    val fullscreenBackStackEntry by fullscreenNavController.currentBackStackEntryAsState()
    val isFullscreen = fullscreenBackStackEntry?.destination?.route !=
        FullscreenDest.Root::class.qualifiedName

    // Create navigator wrapper with fullscreen navigation callback
    val navigator = remember(defaultNavigator) {
        defaultNavigator.apply {
            booksFeature.setFullscreenNavigationCallback { dest ->
                fullscreenNavController.navigate(dest)
            }
            profileFeature.setFullscreenNavigationCallback { dest ->
                fullscreenNavController.navigate(dest)
            }
            notesFeature.setFullscreenNavigationCallback { dest ->
                fullscreenNavController.navigate(dest)
            }
        }
    }

    val destinations = TopLevelDestination.values()
    val items = destinations.map { stringResource(it.titleResId) }
    val icons = destinations.map { it.icon }
    val selectedIcons = destinations.map { it.selectedIcon }

    Scaffold(
        bottomBar = {
            if (!isFullscreen) {
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
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Tab content (rendered first, behind fullscreen)
            TabsNavigation(
                selectedTabIndex,
                booksNavController,
                innerPadding,
                navigator,
                readingNavController,
                discussNavController,
                notesNavController,
                profileNavController
            )

            // Fullscreen content (overlays when active, rendered on top)
            // Only render NavHost when fullscreen is active to avoid intercepting touches
            if (isFullscreen) {
                NavHost(
                    navController = fullscreenNavController,
                    startDestination = FullscreenDest.Root,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Register empty composable for startDestination
                    composable<FullscreenDest.Root> {
                        // Empty composable - placeholder that doesn't render anything
                    }

                    // Register fullscreen content feature
                    navigator.fullscreenContent.registerGraph(
                        fullscreenNavController,
                        this
                    )
                }
            }
        }
    }

}
