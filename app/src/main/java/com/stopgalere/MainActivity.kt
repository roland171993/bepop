package com.stopgalere

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.stopgalere.navigation.NavGraph
import dagger.hilt.android.AndroidEntryPoint
import com.stopgalere.presentation.theme.StopGalereTheme

/**
 * The single Activity for the app. Hosts the Compose NavGraph,
 * applies the app theme, and configures window insets.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Let Compose handle window insets (status/nav bars)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            StopGalereTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph()
                }
            }
        }
    }
}
