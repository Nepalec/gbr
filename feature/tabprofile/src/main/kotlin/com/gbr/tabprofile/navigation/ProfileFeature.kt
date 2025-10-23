package com.gbr.tabprofile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.gbr.common.navigation.Dest
import com.gbr.common.navigation.SubGraphDest
import com.gbr.common.network.Feature
import com.gbr.tabprofile.screen.ProfileScreen
import javax.inject.Inject
import javax.inject.Singleton

interface ProfileFeature : Feature

@Singleton
class ProfileFeatureImpl @Inject constructor() : ProfileFeature {
    override fun registerGraph(
        navHostController: NavHostController,
        navGraphBuilder: NavGraphBuilder
    ) {
        navGraphBuilder.navigation<SubGraphDest.Profile>(startDestination = Dest.Profile) {
            composable<Dest.Profile> {
                ProfileScreen(
                    onNavigateBack = { navHostController.popBackStack() },
                    onNavigateToSettings = { navHostController.navigate(Dest.Settings) }
                )
            }
        }
    }
}
