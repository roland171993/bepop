package com.stopgalere.presentation.viewmodel.coverletter

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.RolandAssoh.stopgalere.ci.R
import com.stopgalere.data.remote.NetworkMonitor
import com.stopgalere.domain.repository.CoverLetterRepoInterface
import com.stopgalere.navigation.Route
import com.stopgalere.presentation.ui.common.UiText
import com.stopgalere.presentation.ui.coverletter.components.CoverLetterUi
import com.stopgalere.presentation.ui.coverletter.components.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject

@HiltViewModel
class CoverLetterDetailViewModel @Inject constructor(
    private val repo: CoverLetterRepoInterface,
    private val network: NetworkMonitor,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val coverLetterId: String =
        checkNotNull(savedStateHandle.get<String>(Route.CoverLetterDetail.ARG_ID)) {
            "CoverLetterDetailViewModel requires '${Route.CoverLetterDetail.ARG_ID}' in SavedStateHandle"
        }

    // Backing UI state
    private val _uiState =
        MutableStateFlow<CoverLetterDetailUiState>(CoverLetterDetailUiState.Loading)

    val screenState: StateFlow<CoverLetterDetailScreenState> =
        combine(_uiState, network.isOnline) { ui, online ->
            CoverLetterDetailScreenState(ui = ui, isOnline = online)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CoverLetterDetailScreenState(
                CoverLetterDetailUiState.Loading,
                isOnline = false
            )
        )

    init {
        viewModelScope.launch {
            repo.coverLetterById(coverLetterId)
                .onStart { _uiState.value = CoverLetterDetailUiState.Loading }
                .catch { e ->
                    _uiState.value = CoverLetterDetailUiState.Error(mapThrowableToUiText(e))
                }
                .collectLatest { cover ->
                    _uiState.value = cover
                        ?.let { CoverLetterDetailUiState.Success(it.toUi()) }
                        ?: CoverLetterDetailUiState.Error(
                            UiText.Resource(R.string.screen_cover_detail_not_loaded)
                        )
                }
        }
    }

    private fun mapThrowableToUiText(t: Throwable): UiText {
        return when (t) {
            is IOException -> UiText.Resource(R.string.no_internet)
            else -> {
                val msg = t.message?.takeIf { it.isNotBlank() }
                msg?.let { UiText.DynamicString(it) }
                    ?: UiText.Resource(R.string.error_unknown)
            }
        }
    }
}

@Immutable
sealed interface CoverLetterDetailUiState {
    data object Loading : CoverLetterDetailUiState
    data class Error(val message: UiText) : CoverLetterDetailUiState
    data class Success(val coverLetter: CoverLetterUi) : CoverLetterDetailUiState
}

@Immutable
data class CoverLetterDetailScreenState(
    val ui: CoverLetterDetailUiState,
    val isOnline: Boolean
)
