package com.stopgalere.presentation.ui.splash

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import com.stopgalere.presentation.viewmodel.splash.SplashUiState

/*
  Runtime permissions for Splash:
  - Location: request BOTH COARSE + FINE (Android requirement)
  - Notifications: POST_NOTIFICATIONS on Android 13+ (runtime)

  Re-asks when ViewModel re-enters RequestPermissions.
 */
@Composable
fun PermissionRequestHandler(
    uiState: SplashUiState,
    onPermissionsResult: (allGranted: Boolean) -> Unit
) {
    var hasRequested by remember { mutableStateOf(false) }

    // Always request both COARSE and FINE when you need fine location.
    val baseLocationPerms = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Build the full set based on SDK
    val permissions = remember {
        if (Build.VERSION.SDK_INT >= 33) {
            // Android 13+: media split + notifications
            baseLocationPerms + arrayOf(
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            // Older: legacy external storage, no runtime notifications
            baseLocationPerms
        }
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        // --- Location: OK if either COARSE or FINE granted (use fine-only if you truly need precise)
        val coarseGranted = results[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val fineGranted   = results[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val locationOk    = coarseGranted || fineGranted


        // --- Notifications (runtime only on 33+):
        val notificationsOk = if (Build.VERSION.SDK_INT >= 33) {
            results[Manifest.permission.POST_NOTIFICATIONS] == true
        } else {
            true
        }

        onPermissionsResult(locationOk  && notificationsOk)
    }

    // If VM re-requests permissions, allow re-launch
    LaunchedEffect(uiState) {
        if (uiState is SplashUiState.RequestPermissions) {
            hasRequested = false
        }
    }

    // Launch dialog once per request cycle
    LaunchedEffect(uiState, hasRequested) {
        if (uiState is SplashUiState.RequestPermissions && !hasRequested) {
            hasRequested = true
            launcher.launch(permissions)
        }
    }
}
