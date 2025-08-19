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
import com.stopgalere.data.remote.dto.JobsResponse

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
        // Determine which page to load
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val anchor = state.anchorPosition?.let { pos ->
                    state.closestItemToPosition(pos)?.id
                        ?.let { id -> keysDao.remoteKeysById(id)?.nextKey?.minus(1) }
                } ?: 1
                anchor
            }
            LoadType.PREPEND -> {
                val firstId = state.firstItemOrNull()?.id
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                val prev = keysDao.remoteKeysById(firstId)?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                prev
            }
            LoadType.APPEND -> {
                val lastId = state.lastItemOrNull()?.id
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                val next = keysDao.remoteKeysById(lastId)?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                next
            }
        }

        val pageSize = state.config.pageSize

        // Call API — only the requested page is downloaded
        val response: JobsResponse = api.getJobs(
            page = page,
            limit = pageSize,
            query = query
        )

        val items = response.jobs.orEmpty()
        val current = response.pagination?.page ?: page
        val totalPages = response.pagination?.totalPages ?: current

        // Derive prev/next safely
        val prevKey = if (current > 1) current - 1 else null
        val nextKey = if (current < totalPages) current + 1 else null

        // Persist page to DB inside a single transaction
        db.withTransaction {
            if (loadType == LoadType.REFRESH) {
                keysDao.clearKeys()
                jobDao.clearAll()
            }

            // Upsert jobs (null-safety + minimal fields)
            jobDao.upsertAll(
                items.mapNotNull { dto ->
                    val id = dto.id ?: return@mapNotNull null
                    JobEntity(
                        id = id,
                        title = dto.title ?: "(no title)",
                        city = dto.city,
                        date = dto.dateAdded
                    )
                }
            )

            // Store the same prev/next for each job in this page
            keysDao.insertAll(
                items.mapNotNull { dto ->
                    val id = dto.id ?: return@mapNotNull null
                    JobRemoteKeys(jobId = id, prevKey = prevKey, nextKey = nextKey)
                }
            )
        }

        MediatorResult.Success(endOfPaginationReached = nextKey == null || items.isEmpty())
    } catch (t: Throwable) {
        MediatorResult.Error(t)
    }
}

/** Helpers to safely access first/last loaded items */
private fun <T : Any> PagingState<Int, T>.firstItemOrNull(): T? =
    pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()

private fun <T : Any> PagingState<Int, T>.lastItemOrNull(): T? =
    pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
