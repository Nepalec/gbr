package com.gbr.tabprofile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.gbr.common.navigation.Dest
import com.gbr.common.navigation.FullscreenDest
import com.gbr.common.navigation.SubGraphDest
import com.gbr.common.network.Feature
import com.gbr.tabprofile.screen.ProfileScreen
import javax.inject.Inject
import javax.inject.Singleton

interface ProfileFeature : Feature {
    fun setFullscreenNavigationCallback(callback: (FullscreenDest) -> Unit)
}

@Singleton
class ProfileFeatureImpl @Inject constructor() : ProfileFeature {
    private var onNavigateToFullscreen: ((FullscreenDest) -> Unit)? = null

    override fun setFullscreenNavigationCallback(callback: (FullscreenDest) -> Unit) {
        onNavigateToFullscreen = callback
    }

    override fun registerGraph(
        navHostController: NavHostController,
        navGraphBuilder: NavGraphBuilder
    ) {
        navGraphBuilder.navigation<SubGraphDest.Profile>(startDestination = Dest.Profile) {
            composable<Dest.Profile> {
                ProfileScreen(
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
