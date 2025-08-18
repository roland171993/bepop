package com.stopgalere.presentation.ui.main

import androidx.compose.material.*
import androidx.compose.material.DrawerValue
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stopgalere.presentation.ui.main.components.DrawerContent
import kotlinx.coroutines.launch

@Composable
fun MainScreenPreview() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val drawerWidth = 150.dp

    ModalDrawer(
        drawerState = drawerState,
        drawerBackgroundColor = Color.Transparent,
        drawerShape = RectangleShape,
        drawerElevation = 0.dp,
        drawerContent = {
            DrawerContent(width = drawerWidth, onItemSelected = { /* no-op */ })
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
            onSetSearchActive = { /* no-op */ },
            jobs = listOf(
                JobUi("Abidjan Abidjan", "Abidjan", "TECHNICO-COMMERCIAUX", "30-09-2017"),
                JobUi("COCODY", "Côte d'Ivoire", "COMMERCIAL B TO B", "04-09-2017")
            )
        )
    }
}

// You can create as many size/dark-mode previews as you like, all calling MainScreenPreview():
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