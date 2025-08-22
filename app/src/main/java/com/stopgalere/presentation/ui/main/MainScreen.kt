package com.stopgalere.presentation.ui.main

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.DrawerValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberDrawerState
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
import androidx.paging.compose.collectAsLazyPagingItems
import com.stopgalere.presentation.ui.main.components.AppBarSearchField
import com.stopgalere.presentation.ui.main.components.MainScreenContent
import com.stopgalere.presentation.viewmodel.MainViewModel


@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val drawerWidth = 150.dp

    val isSearchOpen by viewModel.isSearchOpen.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()

    val jobsPaging = viewModel.jobs.collectAsLazyPagingItems()

    println("SEARCH Main ")

    ModalDrawer(
        drawerState = drawerState,
        modifier = Modifier.statusBarsPadding(),
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
                            scaffoldState.snackbarHostState
                                .showSnackbar("Bientôt disponible")
                        } else {
                            navController.navigate(route)
                        }
                    }
                }
            )
        }
    ) {
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
            onRefresh = { jobsPaging.refresh() }
        )
    }
}

