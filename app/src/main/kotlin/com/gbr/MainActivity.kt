package com.gbr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gbr.designsystem.theme.SemestaUIKitTheme
import com.gbr.navigation.GbrNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var isDarkTheme by remember {
                mutableStateOf(true)
            }
            
            SemestaUIKitTheme(darkTheme = isDarkTheme) {
                GbrNavHost()
            }
        }
    }
}
