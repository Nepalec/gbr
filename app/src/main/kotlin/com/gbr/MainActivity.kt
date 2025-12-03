package com.gbr

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.gbr.designsystem.theme.SemestaUIKitTheme
import com.gbr.model.theme.DarkThemeConfig
import com.gbr.navigation.DefaultNavigator
import com.gbr.navigation.GbrNavHost
import com.gbr.tabbooks.screen.SplashScreen
import com.gbr.ui.SnackbarHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var defaultNavigator: DefaultNavigator

    @Inject
    lateinit var snackbarHelper: SnackbarHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        enableEdgeToEdge()

        setContent {
            val mainViewModel: MainViewModel = hiltViewModel()
            val isLoading by mainViewModel.isLoading.collectAsState()
            val message by mainViewModel.message.collectAsState()
            val appTheme by mainViewModel.appTheme.collectAsState()

            val shouldUseDarkTheme = rememberDarkTheme(appTheme)
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(Unit) {
                snackbarHelper.events.collect { event ->
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.actionLabel,
                        duration = event.duration
                    )
                }
            }

            SemestaUIKitTheme(darkTheme = shouldUseDarkTheme) {
                if (isLoading) {
                    SplashScreen(
                        message = message
                    )
                } else {
                    GbrNavHost(
                        defaultNavigator = defaultNavigator,
                        snackbarHostState = snackbarHostState
                    )
                }
            }
        }
    }
}

@Composable
private fun rememberDarkTheme(themeConfig: DarkThemeConfig): Boolean {
    val isSystemDark = isSystemInDarkTheme()

    return when (themeConfig) {
        DarkThemeConfig.DARK -> true
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.FOLLOW_SYSTEM -> isSystemDark
    }
}
