package com.stopgalere.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.stopgalere.data.local.AppDatabase
import com.stopgalere.data.model.JobEntity
import com.stopgalere.data.model.JobRemoteKeys
import com.stopgalere.data.remote.ApiService

@OptIn(ExperimentalPagingApi::class)
class JobRemoteMediator(
    private val db: AppDatabase,
    private val api: ApiService,
    private val query: String?
) : RemoteMediator<Int, JobEntity>() {

    private val jobDao = db.jobDao()
    private val keysDao = db.jobRemoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, JobEntity>
    ): MediatorResult = try {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val anchor = state.anchorPosition?.let { pos ->
                    state.closestItemToPosition(pos)?.id?.let { keysDao.remoteKeysById(it)?.nextKey?.minus(1) }
                } ?: 1
                anchor
            }
            LoadType.PREPEND -> {
                val firstId = state.firstItemOrNull()?.id ?: return MediatorResult.Success(endOfPaginationReached = true)
                val key = keysDao.remoteKeysById(firstId)?.prevKey ?: return MediatorResult.Success(true)
                key
            }
            LoadType.APPEND -> {
                val lastId = state.lastItemOrNull()?.id ?: return MediatorResult.Success(endOfPaginationReached = true)
                val key = keysDao.remoteKeysById(lastId)?.nextKey ?: return MediatorResult.Success(true)
                key
            }
        }

        val pageSize = state.config.pageSize
        val response = api.getJobs(page = page, pageSize = pageSize, query = query)
        val items = response.items

        db.withTransaction {
            if (loadType == LoadType.REFRESH) {
                keysDao.clearKeys()
                jobDao.clearAll()
            }

            val prev = response.prevPage
            val next = response.nextPage

            jobDao.upsertAll(items.map { dto ->
                JobEntity(id = dto.id, title = dto.title, city = dto.city, date = dto.date)
            })

            keysDao.insertAll(items.map { dto ->
                JobRemoteKeys(jobId = dto.id, prevKey = prev, nextKey = next)
            })
        }

        MediatorResult.Success(endOfPaginationReached = response.nextPage == null || items.isEmpty())
    } catch (t: Throwable) {
        MediatorResult.Error(t)
    }
}
