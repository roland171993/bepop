package com.stopgalere.presentation.ui.coverletter.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.RolandAssoh.stopgalere.ci.R
import com.stopgalere.presentation.theme.Gray101
import com.stopgalere.presentation.ui.main.components.NoContentPlaceholder

@Composable
fun CoverLetterList(
    covers: LazyPagingItems<CoverLetterUi>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    listState: LazyListState,
    onItemClick: (CoverLetterUi) -> Unit = {}
) {
    when {
        covers.loadState.refresh is LoadState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
        covers.loadState.refresh is LoadState.Error -> {
            val e = covers.loadState.refresh as LoadState.Error
            Text("Erreur: ${e.error.message ?: "Inconnue"}", color = Color.Red)
        }
        covers.itemCount == 0 -> {
            NoContentPlaceholder(
                modifier = Modifier
                    .fillMaxSize()
                    .semantics { testTag = "EmptyCoverLetters" }
            )
        }
        else -> {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(color = Gray101)
                    .navigationBarsPadding()
                    .semantics { testTag = "CoverLetterList" },
                contentPadding = contentPadding,
                state = listState
            ) {
                items(count = covers.itemCount,
                    key = { index -> covers.peek(index)?.id ?: index }
                ) { index ->
                    covers[index]?.let { cover ->
                        CoverLetterItem(cover = cover, onClick = onItemClick)
                    }
                }
                item {
                    when (covers.loadState.append) {
                        is LoadState.Loading -> LinearProgressIndicator(Modifier.fillMaxWidth())
                        is LoadState.Error -> Text(stringResource(R.string.job_list_no_more), modifier = Modifier.padding(16.dp))
                        else -> {}
                    }
                }
            }
        }
    }
}
