package com.stopgalere.presentation.ui.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.stopgalere.presentation.theme.StopGalereTheme
import com.stopgalere.presentation.viewmodel.SplashEvent
import com.stopgalere.presentation.viewmodel.SplashViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Orchestrates:
 *  1. UI rendering
 *  2. Permission flow
 *  3. Navigation side-effects
 */
@Composable
fun SplashScreen(
    navController: NavHostController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            if (event is SplashEvent.Navigate) {
                navController.navigate(event.route) {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
    }

    // 1. Content
    SplashScreenContent(modifier = Modifier)

    // 2. Permissions
    PermissionRequestHandler(
        uiState = uiState,
        onPermissionsResult = viewModel::onPermissionsResult
    )
}

/**
 * Default preview wrapped in your app theme.
 */
@Preview(showBackground = true, name = "Default Splash")
@Composable
fun SplashScreenDevicePreviews() {
    StopGalereTheme {
        SplashScreenContent()
    }
}
