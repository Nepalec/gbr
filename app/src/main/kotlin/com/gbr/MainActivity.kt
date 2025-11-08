package com.gbr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.gbr.designsystem.theme.SemestaUIKitTheme
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
            val shouldUseDarkTheme by mainViewModel.shouldUseDarkTheme.collectAsState()

            // Set the activity reference for theme monitoring
            mainViewModel.setActivity(this)

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
