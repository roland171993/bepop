package com.stopgalere.presentation.ui.main.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import com.RolandAssoh.stopgalere.ci.R
import com.stopgalere.presentation.ui.job.JobList
import com.stopgalere.presentation.ui.job.JobUi


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
    isDrawerOpen: Boolean,
    isSearchOpen: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onNavClick: () -> Unit,
    onSetSearchActive: (Boolean) -> Unit,
    jobs: LazyPagingItems<JobUi>
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

        JobList(jobs = jobs, modifier = Modifier.fillMaxSize())
    }
}