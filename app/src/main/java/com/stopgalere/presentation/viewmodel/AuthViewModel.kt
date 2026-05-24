package com.stopgalere.presentation.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.stopgalere.domain.repository.AuthRepoInterface
import com.stopgalere.domain.usecase.auth.GetProfileUseCase
import com.stopgalere.domain.usecase.auth.LoginUseCase
import com.stopgalere.domain.usecase.auth.RegisterUseCase
import com.stopgalere.domain.usecase.auth.UpdateProfileUseCase
import com.stopgalere.presentation.ui.auth.AuthSuccessEvent
import com.stopgalere.presentation.ui.auth.AuthUiState
import com.stopgalere.util.AppConstants.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.Instant
import javax.inject.Inject

/**
 * ViewModel shared by Login, Register, Profile and UpdateProfile screens.
 *
 * Pattern: one [StateFlow<AuthUiState>] drives all screens.
 * After consuming a [AuthSuccessEvent] the screen calls [consumeSuccessEvent].
 *
 * Memory-leak note: no Context/Activity/View references are held.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo:             AuthRepoInterface,
    private val loginUseCase:     LoginUseCase,
    private val registerUseCase:  RegisterUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // ----------------------------------------------------------------
    // Login
    // ----------------------------------------------------------------
    fun login(email: String, password: String) {
        launch("login") {
            val response = loginUseCase(email, password)
            _uiState.update { it.copy(user = response.user, successEvent = AuthSuccessEvent.LoggedIn) }
        }
    }

    // ----------------------------------------------------------------
    // Register
    // ----------------------------------------------------------------
    fun register(
        firstName: String,
        lastName:  String,
        age:       Int?,
        email:     String,
        phone:     String,
        password:  String
    ) {
        launch("register") {
            val response = registerUseCase(firstName, lastName, age, email, phone, password)
            _uiState.update { it.copy(user = response.user, successEvent = AuthSuccessEvent.LoggedIn) }
        }
    }

    // ----------------------------------------------------------------
    // OAuth (Google / Apple via Firebase ID token)
    // ----------------------------------------------------------------
    fun oauthSignIn(firebaseIdToken: String) {
        launch("oauthSignIn") {
            val response = repo.oauthSignIn(firebaseIdToken)
            _uiState.update { it.copy(user = response.user, successEvent = AuthSuccessEvent.LoggedIn) }
        }
    }

    // ----------------------------------------------------------------
    // Load profile
    // ----------------------------------------------------------------
    fun loadProfile() {
        launch("loadProfile") {
            val user = getProfileUseCase()
            _uiState.update { it.copy(user = user) }
        }
    }

    // ----------------------------------------------------------------
    // Update profile text fields
    // ----------------------------------------------------------------
    fun updateProfile(
        firstName: String?,
        lastName:  String?,
        age:       Int?,
        phone:     String?
    ) {
        launch("updateProfile") {
            val user = updateProfileUseCase(firstName, lastName, age, phone)
            _uiState.update {
                it.copy(user = user, successEvent = AuthSuccessEvent.ProfileUpdated)
            }
        }
    }

    // ----------------------------------------------------------------
    // Upload profile photo
    // ----------------------------------------------------------------
    fun uploadPhoto(file: File) {
        launch("uploadPhoto") {
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("photo", file.name, requestBody)
            val user = repo.uploadPhoto(part)
            _uiState.update {
                it.copy(user = user, successEvent = AuthSuccessEvent.ProfileUpdated)
            }
        }
    }

    // ----------------------------------------------------------------
    // Logout
    // ----------------------------------------------------------------
    fun logout() {
        log("logout")
        repo.logout()
        _uiState.update { AuthUiState(successEvent = AuthSuccessEvent.LoggedOut) }
    }

    // ----------------------------------------------------------------
    // Consume success event (call once from LaunchedEffect)
    // ----------------------------------------------------------------
    fun consumeSuccessEvent() {
        _uiState.update { it.copy(successEvent = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // ----------------------------------------------------------------
    // Internal helpers
    // ----------------------------------------------------------------
    private fun launch(action: String, block: suspend () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }
            try {
                block()
            } catch (e: Exception) {
                val ts = Instant.now()
                log("$action error: ${e.message}")
                FirebaseCrashlytics.getInstance().recordException(e)
                _uiState.update { it.copy(errorMessage = e.message ?: "Unexpected error.") }
            } finally {
                _uiState.update { it.copy(loading = false) }
            }
        }
    }

    private fun log(msg: String) =
        Log.d(TAG, "[${Instant.now()}] [AuthViewModel] $msg")
}
