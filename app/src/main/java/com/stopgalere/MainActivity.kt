package com.stopgalere

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.stopgalere.navigation.NavGraph
import dagger.hilt.android.AndroidEntryPoint
import com.stopgalere.presentation.theme.StopGalereTheme

/*
  The single Activity for the app. Hosts the Compose NavGraph,
  applies the app theme, and configures window insets.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Let Compose handle window insets (status/nav bars)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            StopGalereTheme {
                val systemUiController = rememberSystemUiController()
                val primaryColor = MaterialTheme.colorScheme.primary
                // Decide whether icons should be dark or light
                val useDarkIcons = primaryColor.luminance() > 0.5f
                SideEffect {
                    systemUiController.setStatusBarColor(
                        color     = primaryColor,
                        darkIcons = useDarkIcons
                    )
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color    = MaterialTheme.colorScheme.background
                ) {
                    NavGraph()
                }
            }
        }
    }
}
