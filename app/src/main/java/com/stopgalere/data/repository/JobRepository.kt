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
import com.stopgalere.util.AppConstants.TAG
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

    override fun getJobs(query: String?, online: Boolean): Flow<PagingData<Job>> {
        // Recommended: placeholders OFF + prefetchDistance >= pageSize
        val pageSize = 15
        val initialLoad = pageSize * 2
        val prefetch = pageSize

        val config = PagingConfig(
            pageSize = pageSize,
            initialLoadSize = initialLoad,
            prefetchDistance = prefetch,
            enablePlaceholders = false
        )

        println("[$TAG] Repo Pager config: pageSize=$pageSize prefetch=$prefetch placeholders=${config.enablePlaceholders} query=$query online=$online")

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

    override fun jobById(id: String): Flow<Job?> =
        db.jobDao().observeById(id).map { it?.toDomain() }
}
