package com.stopgalere.presentation.viewmodel.splash

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopgalere.data.local.Prefs
import com.stopgalere.di.MainDispatcher
import com.stopgalere.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/*
  ViewModel for the Splash screen.
  - Exposes [unsupportedApi] boolean so the Composable doesn’t need sealed types.
  - Uses internal [SplashUiState] for permission gating.
  - Emits one-off [SplashEvent] (navigate / exit).
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val prefs: Prefs,
    @MainDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    // Internal UI state (permission flow)
    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Idle)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    // Simple flag for unsupported API (UI reads this; no sealed import needed)
    private val _unsupportedApi = MutableStateFlow(false)
    val unsupportedApi: StateFlow<Boolean> = _unsupportedApi.asStateFlow()

    // One-off events
    private val _events = MutableSharedFlow<SplashEvent>(replay = 0)
    val events: SharedFlow<SplashEvent> = _events.asSharedFlow()

    init {
        if (Build.VERSION.SDK_INT > 36) {
            // Block devices above API 36 (per your requirement)
            _unsupportedApi.value = true
            _uiState.value = SplashUiState.Unsupported
        } else {
            // Normal splash bootstrap
            viewModelScope.launch(dispatcher) {
                delay(2_000)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    _uiState.value = SplashUiState.RequestPermissions
                } else {
                    navigateNext()
                }
            }
        }
    }

    /** Called when the user acknowledges the “unsupported Android” dialog. */
    fun onUnsupportedOkClicked() {
        viewModelScope.launch { _events.emit(SplashEvent.ExitApp) }
    }

    /** Aggregated result from the permission launcher. */
    fun onPermissionsResult(allGranted: Boolean) {
        if (allGranted) {
            navigateNext()
        } else {
            // User refused permissions → close the app now.
            // Next time the app starts, Splash will request again.
            // Ask again next launch: show snackbar now, UI will exit after it’s shown
            viewModelScope.launch {
                _events.emit(
                    SplashEvent.ShowPermissionSnackbar
                )
            }
        }
    }

    private fun navigateNext() {
        val nextRoute = if (!prefs.getFirstLaunch()) {
            prefs.setFirstLaunch(true)
            Route.Intro.path
        } else {
            Route.Main.path
        }
        viewModelScope.launch { _events.emit(SplashEvent.Navigate(nextRoute)) }
    }
}

/** Internal UI states for Splash. */
sealed interface SplashUiState {
    data object Idle : SplashUiState
    data object RequestPermissions : SplashUiState
    data object Unsupported : SplashUiState
}

/** One-off events emitted by the ViewModel. */
sealed interface SplashEvent {
    data class Navigate(val route: String) : SplashEvent
    data object ShowPermissionSnackbar : SplashEvent
    data object ExitApp : SplashEvent
}
