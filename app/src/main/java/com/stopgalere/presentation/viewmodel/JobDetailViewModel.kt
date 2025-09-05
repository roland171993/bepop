package com.stopgalere.presentation.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.RolandAssoh.stopgalere.ci.R
import com.stopgalere.data.remote.NetworkMonitor
import com.stopgalere.domain.repository.JobRepoInterface
import com.stopgalere.navigation.Route // <-- use the NavGraph key, don't re-declare
import com.stopgalere.presentation.ui.common.UiText
import com.stopgalere.presentation.ui.job.JobUi
import com.stopgalere.presentation.ui.job.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException

@HiltViewModel
class JobDetailViewModel @Inject constructor(
    private val repo: JobRepoInterface,
    private val network: NetworkMonitor,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val jobId: String = checkNotNull(savedStateHandle.get<String>(Route.JobDetail.ARG_ID)) {
        "JobDetailViewModel requires '${Route.JobDetail.ARG_ID}' in SavedStateHandle"
    }

    // Backing UI state (hot, survives configuration changes)
    private val _uiState = MutableStateFlow<JobDetailUiState>(JobDetailUiState.Loading)

    val screenState: StateFlow<JobDetailScreenState> =
        combine(_uiState, network.isOnline) { ui, online ->
            JobDetailScreenState(ui = ui, isOnline = online)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = JobDetailScreenState(JobDetailUiState.Loading, isOnline = false)
        )

    init {
        viewModelScope.launch {
            repo.jobById(jobId)
                .onStart {
                    _uiState.value = JobDetailUiState.Loading
                }
                .catch { e ->
                    _uiState.value = JobDetailUiState.Error(mapThrowableToUiText(e))
                }
                .collectLatest { job ->
                    _uiState.value = job
                        ?.let { JobDetailUiState.Success(it.toUi()) }
                        ?: JobDetailUiState.Error(UiText.Resource(R.string.screen_job_detail_not_loaded))
                }
        }
    }

    // Map exceptions to user-facing, localized messages
    private fun mapThrowableToUiText(t: Throwable): UiText {
        return when (t) {
            is IOException -> UiText.Resource(R.string.no_internet) // you already have this string
            else -> {
                val msg = t.message?.takeIf { it.isNotBlank() }
                msg?.let { UiText.DynamicString(it) } ?: UiText.Resource(R.string.error_unknown)
            }
        }
    }
}



@Immutable
sealed interface JobDetailUiState {
    data object Loading : JobDetailUiState
    data class Error(val message: UiText) : JobDetailUiState
    data class Success(val job: JobUi) : JobDetailUiState
}

@Immutable
data class JobDetailScreenState(
    val ui: JobDetailUiState,
    val isOnline: Boolean
)