package com.gbr.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.gbr.common.navigation.FullscreenDest
import com.gbr.scrLogin.screen.LoginScreen

fun NavGraphBuilder.Login(
    navHostController: NavHostController
) {
    composable<FullscreenDest.Login> {
        LoginScreen(
            onNavigateBack = { navHostController.popBackStack() }
        )
    }
}

