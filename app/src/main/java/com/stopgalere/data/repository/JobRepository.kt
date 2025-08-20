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
     * When online: uses a RemoteMediator to fetch ONLY the page user views, saving to DB.
     * When offline: uses Room PagingSource only (no network).
     */
    override fun getJobs(query: String?, online: Boolean): Flow<PagingData<Job>> {
        val pageSize = 15

        println("SEARCH online : $online")

        return if (online) {
            val mediator = JobRemoteMediator(db, api, query)
            Pager(
                config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
                remoteMediator = mediator,
                pagingSourceFactory = { jobDao.pagingSource(query) }
            ).flow.map { it.map(JobEntity::toDomain) }
        } else {
            Pager(
                config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
                pagingSourceFactory = { jobDao.pagingSource(query) }
            ).flow.map { it.map(JobEntity::toDomain) }
        }
    }
}
