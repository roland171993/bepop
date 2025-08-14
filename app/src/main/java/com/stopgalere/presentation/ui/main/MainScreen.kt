package com.stopgalere.presentation.ui.main

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.DrawerValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.stopgalere.presentation.ui.main.components.DrawerContent
import com.stopgalere.presentation.ui.main.components.NoContentPlaceholder
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.RolandAssoh.stopgalere.ci.R
import com.stopgalere.presentation.viewmodel.MainViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Close


@Composable
private fun rememberDrawerWidth(fraction: Float = 0.2f): Dp {
    return 150.dp
}
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

    // Real screen keeps the drawer
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
        // Replace sampleJobs() with data from your VM when ready
        val jobs = sampleJobs()

        // Apply simple client-side filtering (replace later with repo/paging)
        val filteredJobs = remember(jobs, query) {
            if (query.isBlank()) jobs
            else jobs.filter { j ->
                j.title.contains(query, ignoreCase = true) ||
                        j.city.contains(query, ignoreCase = true)  ||
                        j.region.contains(query, ignoreCase = true)
            }
        }

        MainScreenContent(
            isDrawerOpen = drawerState.isOpen,
            isSearchOpen = isSearchOpen,
            query = query,
            onQueryChange = viewModel::updateSearchQuery,
            onNavClick = {
                scope.launch{
                    if(drawerState.isOpen) drawerState.close() else drawerState.open()
                }
            },
            onSearchClick = { viewModel.toggleSearch() }, // still used by the app bar icon
            onSetSearchActive = { active ->
                if (active) viewModel.openSearch() else viewModel.closeSearch()
            },
            jobs = filteredJobs
        )
    }
}

@Composable
private fun AppBarSearchField(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .padding(end = 8.dp)
            .heightIn(min = 56.dp)
        ,
        singleLine = true,
        textStyle = TextStyle(fontSize = 16.sp, color = Color.White),
        placeholder = { Text("Rechercher…", color = Color(0xCCFFFFFF)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.White)
                }
            }
        },
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.White,
            cursorColor = Color.White,
            placeholderColor = Color(0xCCFFFFFF),
            leadingIconColor = Color.White,
            trailingIconColor = Color.White,
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() })
    )
}

/** Shared content used by both the real screen and previews (no drawer here). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenContent(
    isDrawerOpen: Boolean,
    isSearchOpen: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onNavClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSetSearchActive: (Boolean) -> Unit,   // NEW
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
                // Left: drawer toggle
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

                // Center: title
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

                // Right: search icon opens search
                IconButton(
                    onClick = { onSetSearchActive(true) },
                    modifier = Modifier.semantics { testTag = "SearchIcon" }
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                }
            } else {
                // While searching
                IconButton(
                    onClick = { onSetSearchActive(false) }, // cancel search
                    modifier = Modifier.semantics { testTag = "SearchBack" }
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Close search", tint = Color.White)
                }

                Spacer(Modifier.width(8.dp))

                // Center: the search field takes the title's place
                AppBarSearchField(
                    modifier = Modifier.weight(1f),
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = {
                        // you filter as user types; optionally close:
                        // onSetSearchActive(false)
                    },
                    onClear = { onQueryChange("") }
                )

                // Right: optional clear action (kept for symmetry)
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.semantics { testTag = "SearchClear" }
                ) {
                    // You can use Icons.Default.Close if you like
                    Icon(Icons.Default.Search, contentDescription = "Do nothing", tint = Color.Transparent)
                }
            }
        }

        if (jobs.isEmpty()) {
            NoContentPlaceholder(modifier = Modifier.fillMaxSize())
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF2F2F2))
                    .semantics { testTag = "MainList" }
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(jobs) { job -> JobRow(job) }
            }
        }
    }
}

// --- UI pieces ---------------------------------------------------------------

private data class JobUi(
    val city: String,
    val region: String,
    val title: String,
    val date: String
)

@Composable
private fun JobRow(job: JobUi) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)) {
        // Small top info row (city | date)
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(job.city, style = MaterialTheme.typography.caption, color = Color(0xFF666666))
            Text(job.date, style = MaterialTheme.typography.caption, color = Color(0xFF666666))
        }

        Spacer(Modifier.height(6.dp))

        // White card with the job title
        Surface(
            shape = RoundedCornerShape(10.dp),
            elevation = 4.dp,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { testTag = "JobCard_${job.title}" }
        ) {
            Box(Modifier.padding(vertical = 16.dp, horizontal = 14.dp)) {
                Text(
                    job.title.uppercase(),
                    style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        // Bottom info row (region)
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(job.region, style = MaterialTheme.typography.caption, color = Color(0xFF666666))
        }
    }
}

// Temporary sample data to preview the UI.
// Hook this to your ViewModel/Flow when you’re ready.
private fun sampleJobs() = listOf(
    JobUi(city = "Abidjan", region = "Abidjan", title = "TECHNICO-COMMERCIAUX", date = "30-09-2017"),
    JobUi(city = "COCODY", region = "Côte d'Ivoire", title = "COMMERCIAL B TO B", date = "04-09-2017"),
    JobUi(city = "—", region = "Côte d'Ivoire", title = "COMMERCIAUX", date = "01-11-2017"),
    JobUi(city = "Abidjan- Cocody", region = "—", title = "CUISINIERS PROFESSIONNELS", date = "04-09-2017"),
    JobUi(city = "—", region = "Côte d'Ivoire", title = "RÉCEPTIONNISTES", date = "30-09-2017")
)

// --------------------------- PREVIEWS ----------------------------------------

/** SMALL PHONE */
@Preview(
    name = "Small – light",
    widthDp = 320, heightDp = 640,
    showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable fun Preview_Main_Small() {
    MaterialTheme { MainScreenPreviewWithDrawerHost() }
}

/** MEDIUM PHONE */
@Preview(
    name = "Medium – light",
    widthDp = 360, heightDp = 740,
    showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable fun Preview_Main_Medium() {
    MaterialTheme { MainScreenPreviewWithDrawerHost() }
}

/** TALL / LARGE PHONE */
@Preview(
    name = "Tall phone – light",
    widthDp = 411, heightDp = 891,
    showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable fun Preview_Main_Tall() {
    MaterialTheme { MainScreenPreviewWithDrawerHost() }
}

/** TABLET (sw600dp+) */
@Preview(
    name = "Tablet – light",
    widthDp = 800, heightDp = 1280,
    showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable fun Preview_Main_Tablet() {
    MaterialTheme { MainScreenPreviewWithDrawerHost() }
}

/** SMALL (DARK MODE) */
@Preview(
    name = "Small – dark",
    widthDp = 320, heightDp = 640,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable fun Preview_Main_Small_Dark() {
    MaterialTheme { MainScreenPreviewWithDrawerHost() }
}

@Composable
private fun MainScreenPreviewWithDrawerHost() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val drawerWidth = rememberDrawerWidth()

    ModalDrawer(
        drawerState = drawerState,
        drawerBackgroundColor = Color.Transparent,
        drawerShape = RectangleShape,
        drawerElevation = 0.dp,
        drawerContent = {
            DrawerContent(width = drawerWidth, onItemSelected = { /* no-op in preview */ })
        }
    ) {
        MainScreenContent(
            isDrawerOpen = drawerState.isOpen,
            isSearchOpen = false,
            query = "",
            onQueryChange = {},
            onNavClick = {
                scope.launch {
                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                }
            },
            onSearchClick = {},
            onSetSearchActive = { /* no-op in preview */ }, // <-- add this line
            jobs = sampleJobs()
        )
    }
}
