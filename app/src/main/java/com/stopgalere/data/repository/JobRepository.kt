// data/repository/JobRepository.kt
package com.stopgalere.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.stopgalere.data.local.AppDatabase
import com.stopgalere.data.model.JobEntity
import com.stopgalere.data.model.toDomain
import com.stopgalere.data.remote.ApiService
import com.stopgalere.domain.model.Job
import com.stopgalere.domain.repository.JobRepoInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class)
@Singleton
class JobRepository @Inject constructor(
    private val db: AppDatabase,
    private val api: ApiService
) : JobRepoInterface {

    private val jobDao = db.jobDao()

    /**
     * DATA layer:
     * - Use Paging3 with RemoteMediator online, or DB-only offline.
     * - critical: initialLoadSize = pageSize (only first page at start)
     * - critical: prefetchDistance = 0 (fetch next page ONLY at the very end)
     *
     * DOMAIN layer does not do pagination.
     * PRESENTATION only consumes PagingData.
     */
    override fun getJobs(query: String?, online: Boolean): Flow<PagingData<Job>> {
        val pageSize = 15

        println("SEARCH online : $online")

        val config = PagingConfig(
            pageSize = pageSize,
            initialLoadSize = pageSize,
            prefetchDistance = 0,     // allowed only if placeholders = true
            enablePlaceholders = true // shows “empty” rows until loaded; needs a count-capable source
        )

        return if (online) {
            val mediator = JobRemoteMediator(db, api, query)
            Pager(
                config = config,
                remoteMediator = mediator,
                pagingSourceFactory = { jobDao.pagingSource(query) }
            ).flow.map { it.map(JobEntity::toDomain) }
        } else {
            Pager(
                config = config,
                pagingSourceFactory = { jobDao.pagingSource(query) }
            ).flow.map { it.map(JobEntity::toDomain) }
        }
    }
}
