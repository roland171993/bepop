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
import com.stopgalere.data.remote.dto.toEntity
import com.stopgalere.domain.validation.JobValidation
import com.stopgalere.domain.validation.common.DateValidation.validateAndFormatDate

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
        val pageToLoad = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                // 1) Try the "last item" path (standard Paging3)
                val nextFromLast = state.lastItemOrNull()?.let { last ->
                    keysDao.remoteKeysById(last.id)?.nextKey
                }
                // 2) Fallback: if last-item keys are missing (first-run timing / ordering quirks),
                //    use the global latest nextKey we stored for this feed.
                val fallbackGlobalNext = keysDao.globalNextKey()

                val next = nextFromLast ?: fallbackGlobalNext
                if (next == null) {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                next
            }
        }

        val response: JobsResponse = api.getJobs(page = pageToLoad, query = query)
        val dtoList = response.jobs.orEmpty()
        val p = response.pagination
        val currentPage = p?.currentPage ?: pageToLoad
        val lastPage    = p?.lastPage    ?: currentPage
        val prevKey     = p?.previousPage
        val nextKey     = p?.nextPage

        // Centralized validation/mapping for both list & detail paths
        val entities = dtoList.mapNotNull { it.toEntity() }

        db.withTransaction {
            if (loadType == LoadType.REFRESH) {
                keysDao.clearKeys()
                jobDao.clearAll()
            }

            if (entities.isNotEmpty()) {
                jobDao.upsertAll(entities)

                // Write per-item keys (standard)
                keysDao.insertAll(
                    entities.map { e ->
                        JobRemoteKeys(
                            jobId = e.id,
                            prevKey = prevKey,
                            nextKey = nextKey
                        )
                    }
                )

                // Also keep/update a single "global" nextKey entry so APPEND can proceed
                // even if the last-item keys aren't discoverable yet.
                keysDao.upsertGlobal(nextKey)

            }
        }

        val endReached = nextKey == null || currentPage >= lastPage || entities.isEmpty()
        if (endReached) {
            // No more pages => drop the GLOBAL key so it never lingers
            db.withTransaction { keysDao.clearGlobal() }
        }
        MediatorResult.Success(endOfPaginationReached = endReached)
    } catch (t: Throwable) {
        MediatorResult.Error(t)
    }
}

/** Helpers to safely access first/last loaded items */
private fun <T : Any> PagingState<Int, T>.lastItemOrNull(): T? =
    pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
