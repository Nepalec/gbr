package com.gbr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.gbr.designsystem.theme.SemestaUIKitTheme
import com.gbr.model.theme.DarkThemeConfig
import com.gbr.navigation.DefaultNavigator
import com.gbr.navigation.GbrNavHost
import com.gbr.tabbooks.screen.SplashScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var defaultNavigator: DefaultNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val mainViewModel: MainViewModel = hiltViewModel()
            val isLoading by mainViewModel.isLoading.collectAsState()
            val message by mainViewModel.message.collectAsState()
            val appTheme by mainViewModel.appTheme.collectAsState()

            val shouldUseDarkTheme = rememberDarkTheme(appTheme)

            SemestaUIKitTheme(darkTheme = shouldUseDarkTheme) {
                if (isLoading) {
                    SplashScreen(
                        message = message
                    )
                } else {
                    GbrNavHost(defaultNavigator = defaultNavigator)
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
