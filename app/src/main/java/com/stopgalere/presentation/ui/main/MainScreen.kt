package com.stopgalere.presentation.ui.main

import android.content.res.Configuration
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
import com.stopgalere.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val withLarge = screenWidth * 0.4f

    // Real screen keeps the drawer
    ModalDrawer(
        drawerState = drawerState,
        modifier = Modifier.statusBarsPadding(),
        drawerContent = {
            DrawerContent(
                width = withLarge,
                onItemSelected = { route ->
                    scope.launch { drawerState.close() }
                    navController.navigate(route)
                }
            )
        }
    ) {
        // Replace sampleJobs() with data from your VM when ready
        val jobs = sampleJobs()

        MainScreenContent(
            isDrawerOpen = drawerState.isOpen,
            onNavClick = {
                scope.launch {
                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                }
            },
            onSearchClick = { /* TODO open search */ },
            jobs = jobs
        )
    }
}

/** Shared content used by both the real screen and previews (no drawer here). */
@Composable
private fun MainScreenContent(
    isDrawerOpen: Boolean,
    onNavClick: () -> Unit,
    onSearchClick: () -> Unit,
    jobs: List<JobUi>
) {
    Column(
        Modifier
            .fillMaxSize()
            .semantics { testTag = "MainScreen" }
    ) {
        // App bar (blue, title centered with weight)
        TopAppBar(
            modifier = Modifier.statusBarsPadding(),
            backgroundColor = Color(0xFF3B8ED0),
            contentColor = Color.White
        ) {
            IconButton(
                onClick = onNavClick,
                modifier = Modifier.semantics { testTag = "NavIcon" }
            ) {
                Icon(
                    imageVector = if (!isDrawerOpen) Icons.Default.Menu else Icons.Default.ArrowBack,
                    contentDescription = "Toggle drawer"
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                "StopGalere CI",
                modifier = Modifier
                    .weight(1f)
                    .semantics { testTag = "AppBarTitle" },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.semantics { testTag = "SearchIcon" }
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search")
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

// Preview wrapper WITHOUT ModalDrawer (no duplication)
@Composable
private fun MainScreenPreviewScaffold() {
    MainScreenContent(
        isDrawerOpen = false,
        onNavClick = {},               // no-op in preview
        onSearchClick = {},            // no-op in preview
        jobs = sampleJobs()
    )
}

/** SMALL PHONE */
@Preview(
    name = "Small – light",
    widthDp = 320, heightDp = 640,
    showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable fun Preview_Main_Small() { MaterialTheme { MainScreenPreviewScaffold() } }

/** MEDIUM PHONE */
@Preview(
    name = "Medium – light",
    widthDp = 360, heightDp = 740,
    showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable fun Preview_Main_Medium() { MaterialTheme { MainScreenPreviewScaffold() } }

/** TALL / LARGE PHONE */
@Preview(
    name = "Tall phone – light",
    widthDp = 411, heightDp = 891,
    showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable fun Preview_Main_Tall() { MaterialTheme { MainScreenPreviewScaffold() } }

/** TABLET (sw600dp+) */
@Preview(
    name = "Tablet – light",
    widthDp = 800, heightDp = 1280,
    showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable fun Preview_Main_Tablet() { MaterialTheme { MainScreenPreviewScaffold() } }

/** SMALL (DARK MODE) */
@Preview(
    name = "Small – dark",
    widthDp = 320, heightDp = 640,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable fun Preview_Main_Small_Dark() { MaterialTheme { MainScreenPreviewScaffold() } }

@Composable
private fun MainScreenPreviewWithDrawerHost() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(width = 280.dp, onItemSelected = { /* no-op in preview */ })
        }
    ) {
        MainScreenContent(
            isDrawerOpen = drawerState.isOpen,
            onNavClick = { scope.launch {
                if (drawerState.isClosed) drawerState.open() else drawerState.close()
            }},
            onSearchClick = {},
            jobs = sampleJobs()
        )
    }
}

@Preview(
    name = "Medium – with Drawer (interactive)",
    widthDp = 360, heightDp = 740,
    showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable
fun Preview_Main_Medium_WithDrawer() {
    MaterialTheme { MainScreenPreviewWithDrawerHost() }
}
