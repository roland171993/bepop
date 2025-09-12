package com.stopgalere.presentation.viewmodel.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopgalere.data.remote.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class AboutViewModel @Inject constructor(
    network: NetworkMonitor
) : ViewModel() {

    // True when the device has internet connectivity.
    val isOnline: StateFlow<Boolean> = network.isOnline.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )
}
