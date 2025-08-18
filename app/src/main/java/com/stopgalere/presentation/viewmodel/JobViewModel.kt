package com.stopgalere.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.stopgalere.domain.usecase.GetJobsUseCase
import com.stopgalere.presentation.ui.job.JobUi
import com.stopgalere.presentation.ui.job.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class JobViewModel @Inject constructor(
    private val getJobs: GetJobsUseCase
) : ViewModel() {

    private val queryState = MutableStateFlow<String?>(null)

    /** Call this from the UI when the user types or submits a search. */
    fun setQuery(q: String?) {
        queryState.value = q?.takeIf { it.isNotBlank() }
    }

    val jobs: Flow<PagingData<JobUi>> =
        queryState
            .debounce(250)
            .distinctUntilChanged()
            .flatMapLatest { q ->
                getJobs(q).map { paging -> paging.map { it.toUi() } }
            }
            .cachedIn(viewModelScope)
}
