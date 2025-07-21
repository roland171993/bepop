package com.stopgalere.presentation.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SplashViewModel @Inject constructor(): ViewModel() {
    private val _showSplash = MutableStateFlow(true)
    val showSplash = _showSplash.asStateFlow()

    init {
        viewModelScope.launch {
            delay(2000) // simulate work
            _showSplash.value = false
        }
    }
}