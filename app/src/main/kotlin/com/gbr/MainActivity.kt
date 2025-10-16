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
import com.gbr.navigation.GbrNavHost
import com.gbr.tabbooks.screen.SplashScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var isDarkTheme by remember {
                mutableStateOf(true)
            }
            
            val mainViewModel: MainViewModel = hiltViewModel()
            val isLoading by mainViewModel.isLoading.collectAsState()
            val message by mainViewModel.message.collectAsState()
            
            SemestaUIKitTheme(darkTheme = isDarkTheme) {
                if (isLoading) {
                    SplashScreen(
                        message = message ?: "Loading..."
                    )
                } else {
                    GbrNavHost()
                }
            }
        }
    }
}
