package com.stopgalere.presentation.ui.auth

import com.stopgalere.data.remote.dto.UserDto

/**
 * UI state model for all authentication screens.
 *
 * [loading]       — true while a network request is in flight
 * [user]          — non-null once the user is authenticated
 * [errorMessage]  — non-null if the last action failed
 * [successEvent]  — consumed once by the screen to trigger navigation
 */
data class AuthUiState(
    val loading:      Boolean = false,
    val user:         UserDto? = null,
    val errorMessage: String?  = null,
    val successEvent: AuthSuccessEvent? = null
)

sealed interface AuthSuccessEvent {
    /** Emitted after register or login — navigate to Main. */
    data object LoggedIn : AuthSuccessEvent
    /** Emitted after profile update / photo upload — stay on Profile. */
    data object ProfileUpdated : AuthSuccessEvent
    /** Emitted after logout — navigate to Login. */
    data object LoggedOut : AuthSuccessEvent
}
