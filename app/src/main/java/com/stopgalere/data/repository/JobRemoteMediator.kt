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
 * DATA layer – keeps pagination state in Room (RemoteKeys),
 * using server-provided pagination: currentPage/lastPage/previousPage/nextPage.
 *
 * MVVM split:
 * - DOMAIN: validation (JobValidation)
 * - DATA: fetch, map, validate, persist, remember next/previous page
 * - PRESENTATION: reads PagingData only (no networking/pagination math)
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

        // 1) Decide which page to load
        val pageToLoad = when (loadType) {
            LoadType.REFRESH -> {
                // First run: page=1 (requirement)
                1
            }
            LoadType.PREPEND -> {
                // We never go "back" for an infinite list; stop here.
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.APPEND -> {
                // Look at the last item’s saved "nextKey" (our in-memory nextPage)
                val lastId = state.lastItemOrNull()?.id
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                val next = keysDao.remoteKeysById(lastId)?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                next
            }
        }

        val pageSize = state.config.pageSize

        // 2) Call API for that exact page
        val response: JobsResponse = api.getJobs(
            page = pageToLoad,
            limit = pageSize,
            query = query
        )

        val dtoList = response.jobs.orEmpty()
        val p = response.pagination
        val currentPage = p?.currentPage ?: pageToLoad
        val lastPage    = p?.lastPage    ?: currentPage
        val prevKey     = p?.previousPage
        val nextKey     = p?.nextPage

        println("JOBS page=$currentPage next=$nextKey last=$lastPage size=${dtoList.size}")

        // 3) Map/validate DTOs → Entities (use DOMAIN rules)
        val entities = dtoList.mapNotNull { dto ->
            if (dto == null) return@mapNotNull null
            val id = dto.id ?: return@mapNotNull null
            val title = dto.title
            val city  = dto.city
            val date  = dto.dateAdded

            if (!JobValidation.isValid(title, city, date)) return@mapNotNull null
            val normalized = JobValidation.validateAndFormatDate(date) ?: return@mapNotNull null

            JobEntity(
                id = id,
                title = title!!.trim(),
                city = city!!.trim(),
                date = normalized,
                description = description,

                // names only (IDs skipped)
                sectorName = sector?.name,
                genderName = gender?.name,
                contractTypeName = contractType?.name,
                workModeName = workMode?.name,

                // author/company
                authorEmail = authorEmail,
                authorWebsite = authorWebsite,
                authorMobile1 = authorMobile1,
                authorLongitude = authorLongitude,
                authorLatitude = authorLatitude,
                company = company,
                companyLogoUrl = companyLogoUrl,

                // misc
                salary = salary,
                experience = experience,
                educationLevel = educationLevel,

                // timestamps we keep
                dateAdded = dateAdded,
                updatedAt = updatedAt
            )
        }

        // 4) Persist to DB in a single transaction
        db.withTransaction {
            if (loadType == LoadType.REFRESH) {
                keysDao.clearKeys()
                jobDao.clearAll()
            }

            if (entities.isNotEmpty()) {
                jobDao.upsertAll(entities)

                // Save the page neighbors alongside each item (our “in-memory” next/prev)
                keysDao.insertAll(
                    entities.map { e ->
                        JobRemoteKeys(
                            jobId = e.id,
                            prevKey = prevKey,
                            nextKey = nextKey
                        )
                    }
                )
            }
        }

        // 5) Stop when there’s no next page OR when current equals last
        val endReached = nextKey == null || currentPage >= lastPage || entities.isEmpty()
        MediatorResult.Success(endOfPaginationReached = endReached)
    } catch (t: Throwable) {
        MediatorResult.Error(t)
    }
}

/** Helpers to safely access first/last loaded items */
private fun <T : Any> PagingState<Int, T>.lastItemOrNull(): T? =
    pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
