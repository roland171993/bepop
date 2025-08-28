package com.stopgalere.presentation.viewmodel

package com.stopgalere.presentation.ui.job

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.stopgalere.domain.repository.JobRepoInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class JobsViewModel @Inject constructor(
    private val repo: JobRepoInterface
) : ViewModel() {
    val jobs: Flow<PagingData<JobUi>> =
        repo.jobsPaged(query = null, online = true)
            .map { pd -> pd.map { it.toUi() } }
            .cachedIn(viewModelScope)
}
