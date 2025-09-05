package com.stopgalere.presentation.ui.coverletter.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.RolandAssoh.stopgalere.ci.R
import com.stopgalere.presentation.ui.main.components.AppBarSearchField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoverLetterTopBar(
    isSearchOpen: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onSetSearchActive: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    TopAppBar(
        modifier = Modifier.semantics { testTag = "CoverLetterTopBar" },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = {
            if (isSearchOpen) {
                AppBarSearchField(
                    modifier = Modifier.fillMaxSize(),
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = {},
                    onClear = { onQueryChange("") }
                )
            } else {
                Text(
                    text = "Lettres de motivation",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                if (isSearchOpen) onSetSearchActive(false) else onBack()
            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
            }
        },
        actions = {
            if (!isSearchOpen) {
                IconButton(onClick = { onSetSearchActive(true) }) {
                    Icon(Icons.Default.Search, contentDescription = "Rechercher")
                }
            }
        }
    )
}

