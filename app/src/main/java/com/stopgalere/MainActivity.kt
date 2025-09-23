package com.stopgalere

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavGraph
import com.stopgalere.navigation.NavGraph
import com.stopgalere.presentation.theme.StopGalereTheme
import com.stopgalere.presentation.ui.splash.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install the SplashScreen and keep it visible based on ViewModel state
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            splashViewModel.showSplash.value
        }

        setContent {
            StopGalereTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }
        }
    }
}


@Preview(
    name = "Main Screen – Light (EN)",
    showBackground = true,
    showSystemUi = true,
    locale = "en"
)
@Composable
fun MainActivityPreviewLightEN() {
    StopGalereTheme(useDarkTheme = false) {
        Surface(modifier = Modifier.fillMaxSize()) {
            NavGraph(navController = rememberNavController())
        }
    }
}

@Preview(
    name = "Main Screen – Dark (FR)",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    showSystemUi = true,
    locale = "fr-rFR"
)
@Composable
fun MainActivityPreviewDarkFR() {
    StopGalereTheme(useDarkTheme = true) {
        Surface(modifier = Modifier.fillMaxSize()) {
            NavGraph(navController = rememberNavController())
        }
    }
}

