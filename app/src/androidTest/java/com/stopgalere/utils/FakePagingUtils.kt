package com.stopgalere.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf

@Composable
fun <T: Any> fakePagingItems(items: List<T>): LazyPagingItems<T> {
    val pd = remember { PagingData.from(items) }
    return flowOf(pd).collectAsLazyPagingItems()
}
