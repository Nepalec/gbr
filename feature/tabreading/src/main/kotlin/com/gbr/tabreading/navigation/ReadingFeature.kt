package com.gbr.tabreading.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.gbr.common.navigation.Dest
import com.gbr.common.navigation.SubGraphDest
import com.gbr.common.network.Feature
import com.gbr.tabreading.screen.ReadingScreen
import javax.inject.Inject
import javax.inject.Singleton

interface ReadingFeature : Feature

@Singleton
class ReadingFeatureImpl @Inject constructor() : ReadingFeature {
    override fun registerGraph(
        navHostController: NavHostController,
        navGraphBuilder: NavGraphBuilder
    ) {
        navGraphBuilder.navigation<SubGraphDest.Reading>(startDestination = Dest.Reading) {
            composable<Dest.Reading> {
                ReadingScreen(
                    onNavigateBack = { navHostController.popBackStack() },
                    onNavigateToSettings = { navHostController.navigate(Dest.Settings) }
                )
            }
        }
    }
}
