package com.gbr.tabnotes.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.gbr.common.navigation.Dest
import com.gbr.common.navigation.FullscreenDest
import com.gbr.common.navigation.SubGraphDest
import com.gbr.common.network.Feature
import com.gbr.tabnotes.screen.NotesScreen
import javax.inject.Inject
import javax.inject.Singleton

interface NotesFeature : Feature {
    fun setFullscreenNavigationCallback(callback: (FullscreenDest) -> Unit)
}

@Singleton
class NotesFeatureImpl @Inject constructor() : NotesFeature {
    private var onNavigateToFullscreen: ((FullscreenDest) -> Unit)? = null

    override fun setFullscreenNavigationCallback(callback: (FullscreenDest) -> Unit) {
        onNavigateToFullscreen = callback
    }

    override fun registerGraph(
        navHostController: NavHostController,
        navGraphBuilder: NavGraphBuilder
    ) {
        navGraphBuilder.navigation<SubGraphDest.Notes>(startDestination = Dest.Notes) {
            composable<Dest.Notes> {
                NotesScreen(
                    onNavigateBack = { navHostController.popBackStack() },
                    onNavigateToSettings = { navHostController.navigate(Dest.Settings) },
                    onNavigateToLogin = {
                        onNavigateToFullscreen?.invoke(FullscreenDest.Login)
                    }
                )
            }
        }
    }
}
