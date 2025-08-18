package com.stopgalere.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.stopgalere.domain.usecase.GetJobsUseCase
import com.stopgalere.presentation.ui.job.JobUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
@HiltViewModel
class MainViewModel @Inject constructor(
    private val getJobs: GetJobsUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchOpen = MutableStateFlow(false)
    val isSearchOpen: StateFlow<Boolean> = _isSearchOpen.asStateFlow()

    fun updateSearchQuery(new: String) { _searchQuery.value = new }
    fun toggleSearch() { _isSearchOpen.value = !_isSearchOpen.value }
    fun openSearch()   { _isSearchOpen.value = true }
    fun closeSearch()  { _isSearchOpen.value = false }

    // Paging stream driven by search
    val jobs: Flow<PagingData<JobUi>> =
        _searchQuery
            .map { it.trim().ifEmpty { null } }   // null = no filter
            .debounce(250)
            .distinctUntilChanged()
            .flatMapLatest { q ->
                getJobs(q).map { paging -> paging.map { it.toUi() } }
            }
            .cachedIn(viewModelScope)
}

