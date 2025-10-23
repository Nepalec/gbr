package com.gbr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
                        message = message ?: "Loading..."
                    )
                } else {
                    GbrNavHost(defaultNavigator = defaultNavigator)
                }
            }
        }
    }
}
