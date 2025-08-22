package com.stopgalere.presentation.ui.main.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.RolandAssoh.stopgalere.ci.R
import com.stopgalere.presentation.ui.job.JobList
import com.stopgalere.presentation.ui.job.JobUi

/**
 * Stateless main screen content (SRP):
 * - Top app bar
 * - Offline banner
 * - Pull-to-refresh container with job list
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
    isDrawerOpen: Boolean,
    isSearchOpen: Boolean,
    query: String,
    isOnline: Boolean,
    onQueryChange: (String) -> Unit,
    onNavClick: () -> Unit,
    onSetSearchActive: (Boolean) -> Unit,
    jobs: LazyPagingItems<JobUi>,
    onRefresh: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .semantics { testTag = "MainScreen" }
    ) {
        MainTopBar(
            isDrawerOpen = isDrawerOpen,
            isSearchOpen = isSearchOpen,
            query = query,
            onQueryChange = onQueryChange,
            onNavClick = onNavClick,
            onSetSearchActive = onSetSearchActive
        )

        // French offline banner (null-safe & lightweight)
        OfflineBanner(visible = !isOnline)

        // M3 Pull-To-Refresh container
        val isRefreshing = jobs.loadState.refresh is LoadState.Loading
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
            // Keep list stateless and reusable
            JobList(
                jobs = jobs,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/* ---------------- small, reusable bits ---------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopBar(
    isDrawerOpen: Boolean,
    isSearchOpen: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onNavClick: () -> Unit,
    onSetSearchActive: (Boolean) -> Unit
) {
    SmallTopAppBar(
        modifier = Modifier.semantics { testTag = "TopAppBar" },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF3B8ED0),
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        title = {
            if (!isSearchOpen) {
                Text(
                    text = stringResource(R.string.screen_main_app_name),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.semantics { testTag = "AppBarTitle" }
                )
            } else {
                AppBarSearchField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { testTag = "SearchField" },
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = { /* live filtering via query */ },
                    onClear = { onQueryChange("") }
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    if (!isSearchOpen) onNavClick() else onSetSearchActive(false)
                },
                modifier = Modifier.semantics { testTag = if (!isSearchOpen) "NavIcon" else "SearchBack" }
            ) {
                Icon(
                    imageVector = if (!isSearchOpen) {
                        if (!isDrawerOpen) Icons.Default.Menu else Icons.Default.ArrowBack
                    } else Icons.Default.ArrowBack,
                    contentDescription = if (!isSearchOpen) "Toggle drawer" else "Close search"
                )
            }
        },
        actions = {
            if (!isSearchOpen) {
                IconButton(
                    onClick = { onSetSearchActive(true) },
                    modifier = Modifier.semantics { testTag = "SearchIcon" }
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            } else {
                Spacer(Modifier.width(12.dp))
            }
        }
    )
}

@ExperimentalMaterial3Api
@Composable
fun SmallTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
) = TopAppBar(
    title = title,
    modifier = modifier,
    navigationIcon = navigationIcon,
    actions = actions,
    expandedHeight = TopAppBarDefaults.TopAppBarExpandedHeight,
    windowInsets = windowInsets,
    colors = colors,
    scrollBehavior = scrollBehavior,
)


/** M3 banner shown only when offline. */
@Composable
private fun OfflineBanner(visible: Boolean) {
    if (!visible) return
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
    ) {
        Text(
            text = "Vous n’êtes pas connecté à Internet.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .semantics { testTag = "OfflineBanner" },
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}
