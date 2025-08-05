package com.stopgalere.presentation.ui.main.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import com.RolandAssoh.stopgalere.ci.R

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onMenuClick: () -> Unit
) {
    Surface(
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.semantics { testTag = "MenuButton" }
            ) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = stringResource(R.string.screen_main_cd_open_drawer)
                )
            }
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value            = query,
                onValueChange    = onQueryChange,
                placeholder      = { Text(stringResource(R.string.screen_main_search_placeholder)) },
                singleLine       = true,
                leadingIcon      = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier         = Modifier
                    .fillMaxWidth()
                    .semantics { testTag = "SearchTextField" }
            )
        }
    }
}
