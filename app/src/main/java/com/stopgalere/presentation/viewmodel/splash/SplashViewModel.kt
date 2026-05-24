package com.stopgalere.presentation.viewmodel.splash

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopgalere.data.local.Prefs
import com.stopgalere.di.MainDispatcher
import com.stopgalere.domain.repository.AuthRepoInterface
import com.stopgalere.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
  ViewModel for the Splash screen.

  Navigation logic:
    1. Unsupported API (> 36) → show dialog → ExitApp
    2. First launch           → Intro
    3. Has stored JWT         → Main  (user is logged in)
    4. No token               → Login (user must authenticate)
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val prefs:      Prefs,
    private val authRepo:   AuthRepoInterface,
    @MainDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Idle)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    private val _unsupportedApi = MutableStateFlow(false)
    val unsupportedApi: StateFlow<Boolean> = _unsupportedApi.asStateFlow()

    private val _events = MutableSharedFlow<SplashEvent>(replay = 0)
    val events: SharedFlow<SplashEvent> = _events.asSharedFlow()

    init {
        if (Build.VERSION.SDK_INT > 36) {
            _unsupportedApi.value = true
            _uiState.value = SplashUiState.Unsupported
        } else {
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

    fun onUnsupportedOkClicked() {
        viewModelScope.launch { _events.emit(SplashEvent.ExitApp) }
    }

    fun onPermissionsResult(allGranted: Boolean) {
        if (allGranted) {
            navigateNext()
        } else {
            viewModelScope.launch {
                _events.emit(SplashEvent.ShowPermissionSnackbar)
            }
        }
    }

    // ----------------------------------------------------------------
    // Navigation decision tree
    // ----------------------------------------------------------------
    private fun navigateNext() {
        val nextRoute = when {
            !prefs.getFirstLaunch() -> {
                prefs.setFirstLaunch(true)
                Route.Intro.path
            }
            authRepo.isLoggedIn() -> Route.Main.path
            else                  -> Route.Login.path
        }
        viewModelScope.launch { _events.emit(SplashEvent.Navigate(nextRoute)) }
    }
}

// ── State & Events ────────────────────────────────────────────────────────────

sealed interface SplashUiState {
    data object Idle               : SplashUiState
    data object RequestPermissions : SplashUiState
    data object Unsupported        : SplashUiState
}

sealed interface SplashEvent {
    data class Navigate(val route: String) : SplashEvent
    data object ShowPermissionSnackbar     : SplashEvent
    data object ExitApp                    : SplashEvent
}
