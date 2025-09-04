package com.stopgalere.presentation.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopgalere.data.local.Prefs
import com.stopgalere.di.MainDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the splash screen.
 * - Shows splash for 2s
 * - Then either requests permissions or navigates on
 * - Emits a navigation event when done.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val prefs: Prefs,
    @MainDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Idle)
    // Idle → RequestPermissions
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SplashEvent>(replay = 0)
    val events: SharedFlow<SplashEvent> = _events.asSharedFlow()

    init {
        // Show splash, then decide
        viewModelScope.launch(dispatcher) {
            delay(2_000)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                _uiState.value = SplashUiState.RequestPermissions
            } else {
                navigateNext()
            }
        }
    }

    /*
      Call from your Activity/Composable when perms result arrives.
      If granted, continue; otherwise re-request.
     */
    fun onPermissionsResult(allGranted: Boolean) {
        if (allGranted) {
            navigateNext()
        } else {
            _uiState.value = SplashUiState.RequestPermissions
        }
    }

    private fun navigateNext() {
        val nextRoute = if (!prefs.getFirstLaunch()) {
            prefs.setFirstLaunch(true)
            "intro"
        } else {
            "main"
        }
        viewModelScope.launch {
            _events.emit(SplashEvent.Navigate(nextRoute))
        }
    }
}

/** UI states for the splash screen. */
sealed interface SplashUiState {
    /** Still showing logo/timer. */
    object Idle : SplashUiState

    /** Trigger the permissions flow in UI. */
    object RequestPermissions : SplashUiState
}

/** One-off events from [SplashViewModel]. */
sealed interface SplashEvent {
    /** Navigate to the given route and clear splash from backstack. */
    data class Navigate(val route: String) : SplashEvent
}