package com.stopgalere.demo.testing.repository

import com.google.samples.apps.nowinandroid.core.data.Synchronizer
import com.google.samples.apps.nowinandroid.core.data.repository.NewsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.NewsResourceQuery
import com.stopgalere.demo.model.data.NewsResource
import com.stopgalere.demo.model.data.Topic
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

class TestNewsRepository : NewsRepository {

    /**
     * The backing hot flow for the list of topics ids for testing.
     */
    private val newsResourcesFlow: MutableSharedFlow<List<NewsResource>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override fun getNewsResources(query: NewsResourceQuery): Flow<List<NewsResource>> =
        newsResourcesFlow.map { newsResources ->
            var result = newsResources
            query.filterTopicIds?.let { filterTopicIds ->
                result = newsResources.filter {
                    it.topics.map(Topic::id).intersect(filterTopicIds).isNotEmpty()
                }
            }
            query.filterNewsIds?.let { filterNewsIds ->
                result = newsResources.filter { it.id in filterNewsIds }
            }
            result
        }

    /**
     * A test-only API to allow controlling the list of news resources from tests.
     */
    fun sendNewsResources(newsResources: List<NewsResource>) {
        newsResourcesFlow.tryEmit(newsResources)
    }

    override suspend fun syncWith(synchronizer: Synchronizer) = true
}
