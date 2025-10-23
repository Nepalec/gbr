package com.gbr.tabdiscuss.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.gbr.common.navigation.Dest
import com.gbr.common.navigation.SubGraphDest
import com.gbr.common.network.Feature
import com.gbr.tabdiscuss.screen.DiscussScreen
import javax.inject.Inject
import javax.inject.Singleton

interface DiscussFeature : Feature

@Singleton
class DiscussFeatureImpl @Inject constructor() : DiscussFeature {
    override fun registerGraph(
        navHostController: NavHostController,
        navGraphBuilder: NavGraphBuilder
    ) {
        navGraphBuilder.navigation<SubGraphDest.Discuss>(startDestination = Dest.Discuss) {
            composable<Dest.Discuss> {
                DiscussScreen(
                    onNavigateBack = { navHostController.popBackStack() },
                    onNavigateToSettings = { navHostController.navigate(Dest.Settings) }
                )
            }
        }
    }
}
