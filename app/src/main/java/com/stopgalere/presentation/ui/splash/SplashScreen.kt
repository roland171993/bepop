// app/src/main/java/com/stopgalere/presentation/ui/splash/SplashScreen.kt
package com.stopgalere.presentation.ui.splash

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.RolandAssoh.stopgalere.ci.R
import com.stopgalere.R
import com.stopgalere.presentation.theme.StopGalereTheme
import com.stopgalere.presentation.viewmodel.SplashEvent
import com.stopgalere.presentation.viewmodel.SplashUiState
import com.stopgalere.presentation.viewmodel.SplashViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SplashScreen(
    navController: NavHostController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    // Observe UI state
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Handle one-off navigation events
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            if (event is SplashEvent.Navigate) {
                navController.navigate(event.route) {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
    }

    // Permissions launcher
    var permissionsRequested by remember { mutableStateOf(false) }
    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        viewModel.onPermissionsResult(results.values.all { it })
    }

    // When ViewModel asks for perms, launch them (once)
    LaunchedEffect(uiState) {
        if (uiState is SplashUiState.RequestPermissions && !permissionsRequested) {
            permissionsRequested = true
            permissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }

    // UI: just show the splash image full-screen
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.splash_screen),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    val navController = rememberNavController()
    StopGalereTheme {
        SplashScreen(navController = navController)
    }
}

