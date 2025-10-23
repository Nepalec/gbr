package com.gbr.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.gbr.common.navigation.Dest
import com.gbr.common.navigation.SubGraphDest
import com.gbr.common.network.Feature
import com.gbr.settings.screen.SettingsScreen
import javax.inject.Inject
import javax.inject.Singleton

interface SettingsFeature : Feature

@Singleton
class SettingsFeatureImpl @Inject constructor() : SettingsFeature {
    override fun registerGraph(
        navHostController: NavHostController,
        navGraphBuilder: NavGraphBuilder
    ) {
        navGraphBuilder.navigation<SubGraphDest.Settings>(startDestination = Dest.Settings) {
            composable<Dest.Settings> {
                SettingsScreen(
                    onNavigateBack = { navHostController.popBackStack() }
                )
            }
        }
    }
}
