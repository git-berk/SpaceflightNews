package com.berco.spaceflightnews

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import com.berco.spaceflightnews.core.ui.theme.SpaceflightTheme
import com.berco.spaceflightnews.ui.navigation.SpaceflightApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The app ships a single light theme, so bar icons must stay dark even
        // when the device is in night mode.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        // Without this the platform paints a translucent scrim behind the
        // gesture bar, which reads as an opaque strip over the content.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        setContent {
            SpaceflightTheme {
                SpaceflightApp()
            }
        }
    }
}
