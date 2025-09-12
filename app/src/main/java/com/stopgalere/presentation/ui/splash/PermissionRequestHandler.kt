package com.stopgalere.presentation.ui.splash

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import com.stopgalere.presentation.viewmodel.splash.SplashUiState

/**
 * Handles the Android runtime permission flow.
 *
 * @param uiState emitted by SplashViewModel—when it becomes [SplashUiState.RequestPermissions],
 *   this composable will launch the permission dialog exactly once.
 * @param onPermissionsResult callback to inform ViewModel of the user’s decision.
 */
@Composable
fun PermissionRequestHandler(
    uiState: SplashUiState,
    onPermissionsResult: (allGranted: Boolean) -> Unit
) {
    var hasRequested by remember { mutableStateOf(false) }

    // Register a permission launcher
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        // Pass aggregated result back to ViewModel
        onPermissionsResult(results.values.all { it })
    }

    // When the ViewModel signals RequestPermissions, fire the dialog once
    LaunchedEffect(uiState) {
        if (uiState is SplashUiState.RequestPermissions && !hasRequested) {
            hasRequested = true
            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }
}