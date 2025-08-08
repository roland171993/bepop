package com.stopgalere.presentation.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.stopgalere.presentation.ui.main.components.DrawerNavItem
import com.stopgalere.presentation.ui.main.components.DrawerNavItemDefaults
import com.stopgalere.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.TopAppBar
import com.stopgalere.presentation.ui.main.components.DrawerContent

@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Top‐bar title is fixed
    // We don’t need a search field here—just the icon as in your screenshot
    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                width = 280.dp,
                onItemSelected = { route ->
                    scope.launch { drawerState.close() }
                    navController.navigate(route)
                }
            )
        },
        modifier = Modifier.semantics{ testTag = "MainScreen"},
        content = {
            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    backgroundColor = Color(0xFF3B8ED0),
                    contentColor    = Color.White,
                ) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        },
                        modifier = Modifier.semantics { testTag = "NavIcon" }
                    ) {
                        Icon(
                            imageVector   = if (drawerState.isClosed) Icons.Default.Menu else Icons.Default.ArrowBack,
                            contentDescription = "Toggle drawer"
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "StopGalere CI",
                        modifier = Modifier
                            .weight(1f)
                            .semantics { testTag = "AppBarTitle" }
                    )
                    IconButton(
                        onClick = { /* TODO: open search screen */ },
                        modifier = Modifier.semantics { testTag = "SearchIcon" }
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }

                // Empty content placeholder
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF666666))
                        .semantics { testTag = "EmptyPage" }
                )
            }
        }
    )
}
