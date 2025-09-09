package com.stopgalere.presentation.viewmodel.coverletter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.stopgalere.data.remote.NetworkMonitor
import com.stopgalere.domain.usecase.GetCoverLettersUseCase
import com.stopgalere.presentation.ui.coverletter.components.CoverLetterUi
import com.stopgalere.presentation.ui.coverletter.components.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

/*
  ViewModel for the Cover Letter list screen.

  Responsibilities:
  - Holds search query + search panel state (survives process death via SavedStateHandle).
  - Exposes network status as StateFlow (for offline banner).
  - Provides a PagingData stream of CoverLetterUi based on the current query and connectivity.

  Testing:
  - All state is exposed as StateFlow/Flow.
  - SEARCH_DEBOUNCE_MS is @VisibleForTesting for override in tests if needed.
  - No Android framework types are required by the public API.
 */
@HiltViewModel
class CoverLetterViewModel @Inject constructor(
    private val getCoverLetters: GetCoverLettersUseCase,
    private val saved: SavedStateHandle,
    network: NetworkMonitor
) : ViewModel() {

    companion object {
        private const val KEY_QUERY = "cover.query"
        private const val KEY_SEARCH_OPEN = "cover.search_open"
        private const val SEARCH_DEBOUNCE_MS = 300L
    }

    private val _searchQuery = MutableStateFlow(saved[KEY_QUERY] ?: "")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchOpen = MutableStateFlow(saved[KEY_SEARCH_OPEN] ?: false)
    val isSearchOpen: StateFlow<Boolean> = _isSearchOpen.asStateFlow()

    val isOnline: StateFlow<Boolean> = network.isOnline

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
        saved[KEY_QUERY] = newQuery
    }
    fun openSearch()  = setSearch(true)
    fun closeSearch() = setSearch(false)
    fun toggleSearch() = setSearch(!_isSearchOpen.value)
    private fun setSearch(open: Boolean) {
        _isSearchOpen.value = open
        saved[KEY_SEARCH_OPEN] = open
    }

    // Paging stream switches between online/offline sources automatically.
    val covers: Flow<PagingData<CoverLetterUi>> =
        combine(
            _searchQuery
                .map { it.trim().ifEmpty { null } }
                .debounce(SEARCH_DEBOUNCE_MS)
                .distinctUntilChanged(),
            isOnline
        ) { q, online -> q to online }
            .flatMapLatest { (q, online) ->
                getCoverLetters(q, online).map { paging -> paging.map { it.toUi() } }
            }
            .cachedIn(viewModelScope)
}
