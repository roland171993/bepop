package com.stopgalere.presentation.viewmodel

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopgalere.domain.model.Job
import com.stopgalere.domain.repository.JobRepoInterface
import com.stopgalere.presentation.ui.job.JobUi
import com.stopgalere.presentation.ui.job.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
sealed interface JobDetailUiState {
    data object Loading : JobDetailUiState
    data class Error(val message: String?) : JobDetailUiState
    data class Success(val job: JobUi) : JobDetailUiState
}

@HiltViewModel
class JobDetailViewModel @Inject constructor(
    private val repo: JobRepoInterface,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val jobId: String = checkNotNull(savedStateHandle["jobId"]) {
        "jobId must be provided in navigation arguments"
    }

    val uiState = mutableStateOf<JobDetailUiState>(JobDetailUiState.Loading)

    init {
        viewModelScope.launch {
            repo.jobById(jobId)
                .catch { e -> uiState.value = JobDetailUiState.Error(e.message) }
                .collectLatest { job ->
                    uiState.value = job?.let { JobDetailUiState.Success(it.toUi()) }
                        ?: JobDetailUiState.Error("Job not found")
                }
        }
    }
}
