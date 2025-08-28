package com.stopgalere.presentation.ui.main

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.RolandAssoh.stopgalere.ci.R
import com.stopgalere.presentation.ui.main.components.DrawerContent
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.paging.compose.collectAsLazyPagingItems
import com.stopgalere.navigation.navigateToJobDetail
import com.stopgalere.presentation.ui.main.components.AppBarSearchField
import com.stopgalere.presentation.ui.main.components.MainScreenContent
import com.stopgalere.presentation.viewmodel.MainViewModel


@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val drawerWidth = 150.dp

    val isSearchOpen by viewModel.isSearchOpen.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()

    val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val jobsPaging = viewModel.jobs.collectAsLazyPagingItems()
    val welcomeMsg = stringResource(R.string.screen_main_welcome)

    println("SEARCH Main ")

    ModalDrawer(
        drawerState = drawerState,
        modifier = Modifier
            .statusBarsPadding(),
        drawerBackgroundColor = Color.Transparent,
        drawerShape = RectangleShape,
        drawerElevation = 0.dp,
        drawerContent = {
            DrawerContent(
                width = drawerWidth,
                onItemSelected = { route ->
                    scope.launch {
                        drawerState.close()
                        viewModel.closeSearch()
                        if (route == "cv") {
                            snackbarHostState.showSnackbar(welcomeMsg)
                        } else {
                            navController.navigate(route)
                        }
                    }
                }
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            MainScreenContent(
                isDrawerOpen = drawerState.isOpen,
                isSearchOpen = isSearchOpen,
                isOnline = isOnline,
                query = query,
                onQueryChange = viewModel::updateSearchQuery,
                onNavClick = {
                    scope.launch {
                        if (drawerState.isOpen) drawerState.close() else drawerState.open()
                    }
                },
                onSetSearchActive = { active ->
                    if (active) viewModel.openSearch() else viewModel.closeSearch()
                },
                jobs = jobsPaging,
                onRefresh = { jobsPaging.refresh() },
                listState = listState,
                onJobClick = { job ->
                    navController.navigateToJobDetail(job.id)
                }
            )
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
                    .padding(0.dp,0.dp,0.dp,50.dp)
            )
        }

    }


}

