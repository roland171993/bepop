package com.stopgalere.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.stopgalere.data.remote.NetworkMonitor
import com.stopgalere.domain.usecase.GetJobsUseCase
import com.stopgalere.presentation.ui.job.JobUi
import com.stopgalere.presentation.ui.job.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getJobs: GetJobsUseCase,
    private val saved: SavedStateHandle,
    network: NetworkMonitor
) : ViewModel() {

    companion object {
        private const val KEY_QUERY = "main.query"
        private const val KEY_SEARCH_OPEN = "main.search_open"
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
    fun openSearch()  { setSearch(true) }
    fun closeSearch() { setSearch(false) }
    fun toggleSearch() { setSearch(!_isSearchOpen.value) }
    private fun setSearch(open: Boolean) {
        _isSearchOpen.value = open
        saved[KEY_SEARCH_OPEN] = open
    }

    /** Paging stream switches between online/offline sources automatically. */
    val jobs: Flow<PagingData<JobUi>> =
        combine(
            _searchQuery
                .map { it.trim().ifEmpty { null } }
                .debounce(SEARCH_DEBOUNCE_MS)
                .distinctUntilChanged(),
            isOnline
        ) { q, online -> q to online }
            .flatMapLatest { (q, online) ->
                getJobs(q, online).map { paging -> paging.map { it.toUi() } }
            }
            .cachedIn(viewModelScope)
}
