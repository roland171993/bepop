package com.stopgalere.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.stopgalere.data.local.AppDatabase
import com.stopgalere.data.model.toDomain
import com.stopgalere.data.remote.ApiService
import com.stopgalere.domain.model.Job
import com.stopgalere.domain.repository.JobRepoInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobRepository @Inject constructor(
    private val db: AppDatabase,
    private val api: ApiService
) : JobRepoInterface {

    @OptIn(ExperimentalPagingApi::class)
    override fun pagedJobs(query: String?): Flow<PagingData<Job>> {
        val pagingSourceFactory = { db.jobDao().pagingSource(query) }
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false, prefetchDistance = 2),
            remoteMediator = JobRemoteMediator(db, api, query),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { paging -> paging.map { it.toDomain() } }
    }
}
