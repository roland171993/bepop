package com.stopgalere.presentation.ui.coverletter.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoverLetterContent(
    isSearchOpen: Boolean,
    query: String,
    isOnline: Boolean,
    onQueryChange: (String) -> Unit,
    onSetSearchActive: (Boolean) -> Unit,
    onBack: () -> Unit,
    covers: LazyPagingItems<CoverLetterUi>,
    listState: LazyListState,
    onRefresh: () -> Unit,
    onItemClick: (CoverLetterUi) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .semantics { testTag = "CoverLetterScreen" }
    ) {
        CoverLetterTopBar(
            isSearchOpen = isSearchOpen,
            query = query,
            onQueryChange = onQueryChange,
            onSetSearchActive = onSetSearchActive,
            onBack = onBack
        )

        if (!isOnline) {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
            ) {
                Text(
                    text = "Pas de connexion Internet",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        val isRefreshing = covers.loadState.refresh is LoadState.Loading
        val state = rememberPullToRefreshState()

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            state = state,
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = state,
                    isRefreshing = isRefreshing,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier.weight(1f)
        ) {
            CoverLetterList(
                covers = covers,
                listState = listState,
                onItemClick = onItemClick
            )
        }
    }
}
