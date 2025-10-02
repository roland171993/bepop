package com.stopgalere.presentation.ui.main

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.rememberDrawerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.RolandAssoh.stopgalere.ci.R
import com.stopgalere.presentation.ui.main.components.DrawerContent
import com.stopgalere.presentation.ui.main.components.MainScreenContent
import com.stopgalere.presentation.viewmodel.MainUiEvent
import com.stopgalere.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel(),
    onOpenJobDetail: (jobId:String) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val drawerWidth = 150.dp

    val isSearchOpen by viewModel.isSearchOpen.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val showUpload  by viewModel.showUploadDialog.collectAsStateWithLifecycle()

    val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val jobsPaging = viewModel.jobs.collectAsLazyPagingItems()
    val commingSoonMsg = stringResource(R.string.screen_main_comming_soon)
    val context = LocalContext.current
    val mailText = stringResource(R.string.screen_main_mail_us) +
            stringResource(R.string.app_email)

    //  perform platform actions here
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is MainUiEvent.RateApp -> {
                    val appId = context.packageName
                    val market = Intent(Intent.ACTION_VIEW, "market://details?id=$appId".toUri())
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    try {
                        context.startActivity(market)
                    } catch (_: Exception) {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                "https://play.google.com/store/apps/details?id=$appId".toUri()
                            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                }
                is MainUiEvent.OpenHelp -> {
                    snackbarHostState.showSnackbar(commingSoonMsg)
//                    val url = context.getString(R.string.facebook_videos).trim()
//                    context.startActivity(
//                        Intent(Intent.ACTION_VIEW, "http://$url".toUri())
//                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    )
                }
                is MainUiEvent.OpenPage -> {
                    val url = context.getString(R.string.facebook_groupe).trim()
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, "http://$url".toUri())
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
                is MainUiEvent.ResumePage -> snackbarHostState.showSnackbar(commingSoonMsg)
                is MainUiEvent.Navigate -> navController.navigate(event.route)
            }
        }
    }


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
                        println("Route: $route")
                        viewModel.onDrawerRouteSelected(route)
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
                    onOpenJobDetail(job.id)
                }
            )
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
                    .padding(0.dp,0.dp,0.dp,50.dp)
            )
            if (showUpload) {
                AlertDialog(
                    onDismissRequest = { viewModel.dismissUploadDialog() },
                    title = { Text(stringResource(R.string.screen_main_post_job)) },
                    text  = { Text(mailText)},
                    confirmButton = {
                        TextButton(onClick = { viewModel.dismissUploadDialog() }) {
                            Text(stringResource(R.string.ok))
                        }
                    }
                )
            }
        }

    }


}

