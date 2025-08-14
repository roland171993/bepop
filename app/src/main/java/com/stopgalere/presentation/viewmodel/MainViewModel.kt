package com.stopgalere.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    // Existing query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // New: controls visibility of the search bar
    private val _isSearchOpen = MutableStateFlow(false)
    val isSearchOpen: StateFlow<Boolean> = _isSearchOpen.asStateFlow()

    /** Called when the user edits the search field */
    fun updateSearchQuery(new: String) {
        _searchQuery.value = new
    }

    /** Toggle/Show/Hide search bar */
    fun toggleSearch() { _isSearchOpen.value = !_isSearchOpen.value }
    fun openSearch()   { _isSearchOpen.value = true }
    fun closeSearch()  { _isSearchOpen.value = false }
}
