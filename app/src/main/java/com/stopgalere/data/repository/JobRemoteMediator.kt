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
import com.stopgalere.domain.validation.common.DateValidation.validateAndFormatDate
import com.stopgalere.util.AppConstants.TAG

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
                    println("[$TAG] RM.APPEND no nextKey (last=${state.lastItemOrNull()?.id}); declaring end")
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                next
            }
        }

        println("[$TAG] RM.load() type=$loadType lastItem=${state.lastItemOrNull()?.id}")

        val response: JobsResponse = api.getJobs(page = pageToLoad, query = query)
        val dtoList = response.jobs.orEmpty()
        val p = response.pagination
        val currentPage = p?.currentPage ?: pageToLoad
        val lastPage    = p?.lastPage    ?: currentPage
        val prevKey     = p?.previousPage
        val nextKey     = p?.nextPage

        println("JOBS page=$currentPage next=$nextKey last=$lastPage size=${dtoList.size}")
        println("[$TAG] RM.API page=$currentPage prev=$prevKey next=$nextKey dtoSize=${dtoList.size}")

        val entities = dtoList.mapNotNull { dto ->
            val id = dto.id ?: return@mapNotNull null
            val title = dto.title?.trim() ?: return@mapNotNull null
            val city  = dto.city?.trim() ?: return@mapNotNull null
            val dateRaw = dto.dateAdded?.trim() ?: return@mapNotNull null

            JobEntity(
                id = id,
                title = title,
                city = city,
                date = validateAndFormatDate(dateRaw)!!,
                deadline = validateAndFormatDate(dto.deadline?.trim())!!,
                dateAdded = dateRaw,
                description = dto.description?.trim()!!,
                sectorName = dto.sector?.name?.trim()!!,
                genderName = dto.gender?.name?.trim().orEmpty(),
                contractTypeName = dto.contractType?.name?.trim().orEmpty(),
                workModeName = dto.workMode?.name?.trim().orEmpty(),
                authorEmail = dto.authorEmail?.trim().orEmpty(),
                authorWebsite = dto.authorWebsite?.trim().orEmpty(),
                authorMobile1 = dto.authorMobile1?.trim().orEmpty(),
                authorMobile2 = dto.authorMobile2?.trim().orEmpty(),
                authorLongitude = dto.authorLongitude,
                authorLatitude = dto.authorLatitude,
                company = dto.company?.trim()!!,
                companyLogoUrl = dto.companyLogoUrl?.trim().orEmpty(),
                salary = JobValidation.validateAndFormatMoney(dto.salary),
                experience = dto.experience?.trim().orEmpty(),
                educationLevel = dto.educationLevel?.trim().orEmpty()
            )
        }

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

                println("[$TAG] RM.DB after insert: inserted=${entities.size}")
            }
        }

        val endReached = nextKey == null || currentPage >= lastPage || entities.isEmpty()
        println("[$TAG] RM.result end=$endReached because nextKey=$nextKey current=$currentPage last=$lastPage")
        MediatorResult.Success(endOfPaginationReached = endReached)
    } catch (t: Throwable) {
        MediatorResult.Error(t)
    }
}

/** Helpers to safely access first/last loaded items */
private fun <T : Any> PagingState<Int, T>.lastItemOrNull(): T? =
    pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
