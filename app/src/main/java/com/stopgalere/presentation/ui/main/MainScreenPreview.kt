package com.stopgalere.presentation.ui.main

import android.content.res.Configuration
import androidx.compose.material.*
import androidx.compose.material.DrawerValue
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.stopgalere.presentation.ui.job.JobUi
import com.stopgalere.presentation.ui.main.components.DrawerContent
import com.stopgalere.presentation.ui.main.components.MainScreenContent
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Composable
fun MainScreenPreview() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val drawerWidth = 150.dp

    // Fake paging items for preview
    val fakeJobs: LazyPagingItems<JobUi> = previewPagingItems(
        listOf(
            JobUi(id = "1", title = "Android Engineer", city = "Abidjan", date = "2025-08-01"),
            JobUi(id = "2", title = "Kotlin Dev", city = "Côte d'Ivoire", date = "2025-07-22"),
            JobUi(id = "3", title = "COMMERCIAL B TO B", city = "Cocody", date = "2025-07-10")
        )
    )

    ModalDrawer(
        drawerState = drawerState,
        drawerBackgroundColor = Color.Transparent,
        drawerShape = RectangleShape,
        drawerElevation = 0.dp,
        drawerContent = {
            DrawerContent(
                width = drawerWidth,
                onItemSelected = { /* no-op in preview */ }
            )
        }
    ) {
        MainScreenContent(
            isDrawerOpen = drawerState.isOpen,
            isSearchOpen = false,
            query = "",
            onQueryChange = { /* no-op */ },
            onNavClick = {
                scope.launch {
                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                }
            },
            onSetSearchActive = { /* no-op */ },
            jobs = fakeJobs
        )
    }
}

/* ---------- Previews ---------- */

@Preview(
    name = "Phone – light",
    widthDp = 360, heightDp = 740, showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable fun Preview_Phone_Light() { MaterialTheme { MainScreenPreview() } }

@Preview(
    name = "Phone – dark",
    widthDp = 360, heightDp = 740, showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable fun Preview_Phone_Dark() { MaterialTheme { MainScreenPreview() } }

@Preview(
    name = "Tablet – light",
    widthDp = 800, heightDp = 1280, showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable fun Preview_Tablet_Light() { MaterialTheme { MainScreenPreview() } }

/* ---------- Preview helpers ---------- */

@Composable
private fun previewPagingItems(list: List<JobUi>): LazyPagingItems<JobUi> {
    val pd = remember { PagingData.from(list) }
    return flowOf(pd).collectAsLazyPagingItems()
}
