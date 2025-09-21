package com.stopgalere.presentation.ui.splash

import android.app.Activity
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.stopgalere.navigation.Route
import com.stopgalere.presentation.theme.StopGalereTheme
import com.stopgalere.presentation.viewmodel.splash.SplashEvent
import com.stopgalere.presentation.viewmodel.splash.SplashViewModel
import kotlinx.coroutines.flow.collectLatest
import com.RolandAssoh.stopgalere.ci.R


@Composable
fun SplashScreen(
    navController: NavHostController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val activity = LocalContext.current as? Activity
    val unsupported = viewModel.unsupportedApi.collectAsStateWithLifecycle().value
    val snackbarHostState = remember { SnackbarHostState() }
    val permissionMsg = stringResource(R.string.screen_splash_permission_required)

    // One-time events
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is SplashEvent.Navigate -> {
                    navController.navigate(event.route) {
                        popUpTo(Route.Splash.path) { inclusive = true }
                    }
                }
                is SplashEvent.ShowPermissionSnackbar -> {
                    // Material 3 "Compose Toast": show a Snackbar, then exit
                    snackbarHostState.showSnackbar(
                        message = permissionMsg,
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                    activity?.finishAffinity()
                }
                SplashEvent.ExitApp -> {
                    activity?.finishAffinity()
                }
            }
        }
    }

    // If unsupported → show dialog and stop rendering the rest of splash
    if (unsupported) { // ← simple boolean, no sealed-type import
        UnsupportedApiDialog(onOk = { viewModel.onUnsupportedOkClicked() })
        return
    }

    // 1. Content
    SplashScreenContent(
        modifier = Modifier,
        snackbarHostState = snackbarHostState
    )

    // 2. Permissions
    PermissionRequestHandler(
        uiState = uiState,
        onPermissionsResult = viewModel::onPermissionsResult
    )
}


@Preview(showBackground = true, name = "Default Splash")
@Composable
fun SplashScreenDevicePreviews() {
    StopGalereTheme {
        SplashScreenContent()
    }
}
