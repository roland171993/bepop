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
import com.stopgalere.domain.validation.JobValidation

/**
 * RemoteMediator for Jobs.
 *
 * Responsibilities (DATA layer):
 * - Fetch the requested page from the API
 * - Validate raw DTO fields using DOMAIN rules
 * - Persist only valid rows to Room inside a single transaction
 * - Maintain per-item remote keys for append/prepend
 *
 * Separation of concerns:
 * - DOMAIN: JobValidation expresses what a "valid" job is.
 * - DATA: this mediator fetches, filters, persists.
 * - PRESENTATION: reads from Room/Paging; no validation logic there.
 */

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

        println("SEARCH response fired")
        println("SEARCH response jobs ${response.jobs.size}")
        println("SEARCH response pagination ${response.pagination?.page}")

        val items = response.jobs.orEmpty()
        val current = response.pagination?.page ?: page
        val totalPages = response.pagination?.totalPages ?: current

        items.forEachIndexed { index, dto ->
            println("SEARCH Job[$index]: id=${dto.id}, title=${dto.title}, city=${dto.city}, date=${dto.dateAdded}")
        }

        // Derive prev/next safely
        val prevKey = if (current > 1) current - 1 else null
        val nextKey = if (current < totalPages) current + 1 else null

        // Persist page to DB inside a single transaction
        db.withTransaction {
            if (loadType == LoadType.REFRESH) {
                keysDao.clearKeys()
                jobDao.clearAll()
            }

            // Map -> Validate (DOMAIN) -> skip invalid/null safely
            val entities: List<JobEntity> = items.mapNotNull { dto ->
                // Skip null job objects entirely
                if (dto == null) {
                    println("SKIP job: dto is null")
                    return@mapNotNull null
                }

                // ID is required for Room primary key and keys table
                val id = dto.id
                if (id.isNullOrBlank()) {
                    println("SKIP job: missing id (title='${dto.title}', city='${dto.city}', date='${dto.dateAdded}')")
                    return@mapNotNull null
                }

                val title = dto.title
                val city = dto.city
                val date = dto.dateAdded

                // Apply DOMAIN validation rules
                val isValid = JobValidation.isValid(
                    title = title,
                    city = city,
                    date = date
                )

                if (!isValid) {
                    println("SKIP job[$id]: invalid fields -> title='$title', city='$city', date='$date'")
                    return@mapNotNull null
                }

                val frenchDate = JobValidation.validateAndFormatDate(date)
                if (frenchDate == null) {
                    // Defensive: should not happen if isValid already passed, but keep safe
                    println("SKIP job[$id]: normalization failed for date='$date'")
                    return@mapNotNull null
                }

                // At this point all are non-null & valid; trim before saving
                JobEntity(
                    id = id,
                    title = title!!.trim(),
                    city = city!!.trim(),
                    date = frenchDate
                )
            }

            if (entities.isNotEmpty()) {
                jobDao.upsertAll(entities)
                // Insert keys only for the rows we actually persisted
                keysDao.insertAll(
                    entities.map { e ->
                        JobRemoteKeys(
                            jobId = e.id,
                            prevKey = prevKey,
                            nextKey = nextKey
                        )
                    }
                )
            } else {
                println("No valid jobs to persist on page $current")
            }
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
