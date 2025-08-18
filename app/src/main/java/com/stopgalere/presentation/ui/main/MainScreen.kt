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
import com.stopgalere.presentation.viewmodel.JobViewModel
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.ExperimentalMaterial3Api
import com.stopgalere.presentation.ui.main.components.AppBarSearchField
import com.stopgalere.presentation.viewmodel.MainViewModel

@Composable
private fun rememberDrawerWidth(fraction: Float = 0.2f) = 150.dp

// Small UI model for this screen (keep internal to this feature).
internal data class JobUi(
    val city: String,
    val region: String,
    val title: String,
    val date: String
)

@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val drawerWidth = rememberDrawerWidth()

    val isSearchOpen by viewModel.isSearchOpen.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()

    // Replace with VM data when ready
    val jobs = sampleJobs()

    // Simple client-side filter
    val filteredJobs = remember(jobs, query) {
        if (query.isBlank()) jobs
        else jobs.filter { j ->
            j.title.contains(query, ignoreCase = true) ||
                    j.city.contains(query, ignoreCase = true)  ||
                    j.region.contains(query, ignoreCase = true)
        }
    }

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
                    scope.launch { drawerState.close() }
                    viewModel.closeSearch()
                    navController.navigate(route)
                }
            )
        }
    ) {
        MainScreenContent(
            isDrawerOpen = drawerState.isOpen,
            isSearchOpen = isSearchOpen,
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
            jobs = filteredJobs
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
    isDrawerOpen: Boolean,
    isSearchOpen: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onNavClick: () -> Unit,
    onSetSearchActive: (Boolean) -> Unit,
    jobs: List<JobUi>
) {
    Column(
        Modifier
            .fillMaxSize()
            .semantics { testTag = "MainScreen" }
    ) {
        TopAppBar(
            modifier = Modifier.statusBarsPadding(),
            backgroundColor = Color(0xFF3B8ED0),
            contentColor = Color.White,
            elevation = 0.dp
        ) {
            if (!isSearchOpen) {
                IconButton(
                    onClick = onNavClick,
                    modifier = Modifier.semantics { testTag = "NavIcon" }
                ) {
                    Icon(
                        imageVector = if (!isDrawerOpen) Icons.Default.Menu else Icons.Default.ArrowBack,
                        contentDescription = "Toggle drawer",
                        tint = Color.White
                    )
                }

                Spacer(Modifier.width(8.dp))

                Text(
                    stringResource(R.string.screen_main_app_name),
                    modifier = Modifier
                        .weight(1f)
                        .semantics { testTag = "AppBarTitle" },
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                IconButton(
                    onClick = { onSetSearchActive(true) },
                    modifier = Modifier.semantics { testTag = "SearchIcon" }
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                }
            } else {
                IconButton(
                    onClick = { onSetSearchActive(false) },
                    modifier = Modifier.semantics { testTag = "SearchBack" }
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Close search", tint = Color.White)
                }

                Spacer(Modifier.width(8.dp))

                AppBarSearchField(
                    modifier = Modifier.weight(1f),
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = { /* filtering already live */ },
                    onClear = { onQueryChange("") }
                )

                // keep the space balanced
                Spacer(Modifier.width(48.dp))
            }
        }

        MainList(jobs = jobs)
    }
}

// ---------- sample data (preview/dev only) ----------
private fun sampleJobs() = listOf(
    JobUi(city = "Abidjan Abidjan", region = "Abidjan", title = "TECHNICO-COMMERCIAUX", date = "30-09-2017"),
    JobUi(city = "COCODY", region = "Côte d'Ivoire", title = "COMMERCIAL B TO B", date = "04-09-2017"),
    JobUi(city = "—", region = "Côte d'Ivoire", title = "COMMERCIAUX", date = "01-11-2017"),
    JobUi(city = "Abidjan- Cocody", region = "—", title = "CUISINIERS PROFESSIONNELS", date = "04-09-2017"),
    JobUi(city = "—", region = "Côte d'Ivoire", title = "RÉCEPTIONNISTES", date = "30-09-2017")
)
