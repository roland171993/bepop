package com.stopgalere.presentation.ui.coverletter

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.stopgalere.presentation.ui.coverletter.components.CoverLetterContent
import com.stopgalere.presentation.viewmodel.CoverLetterViewModel

@Composable
fun CoverLetterScreen(
    viewModel: CoverLetterViewModel = hiltViewModel(),
    navController: NavHostController,
    onOpenDetail: (String) -> Unit = {}
) {
    val isSearchOpen by viewModel.isSearchOpen.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()

    val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val covers = viewModel.covers.collectAsLazyPagingItems()

    CoverLetterContent(
        isSearchOpen = isSearchOpen,
        query = query,
        isOnline = isOnline,
        onQueryChange = viewModel::updateSearchQuery,
        onSetSearchActive = { if (it) viewModel.openSearch() else viewModel.closeSearch() },
        onBack = ({ navController.popBackStack() }),
        covers = covers,
        listState = listState,
        onRefresh = { covers.refresh() },
        onItemClick = { onOpenDetail(it.id) }
    )
}
