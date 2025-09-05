package com.stopgalere.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.stopgalere.data.local.AppDatabase
import com.stopgalere.data.model.CoverLetterEntity
import com.stopgalere.data.model.CoverLetterRemoteKeys
import com.stopgalere.data.remote.ApiService
import com.stopgalere.data.remote.dto.CoverLettersResponse
import com.stopgalere.domain.validation.CoverLetterValidation
import com.stopgalere.domain.validation.common.SafeText.isSafeText

@OptIn(ExperimentalPagingApi::class)
class CoverLetterRemoteMediator(
    private val db: AppDatabase,
    private val api: ApiService,
    private val query: String?
) : RemoteMediator<Int, CoverLetterEntity>() {

    private val dao = db.coverLetterDao()
    private val keysDao = db.coverLetterRemoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CoverLetterEntity>
    ): MediatorResult = try {
        val pageToLoad = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val lastId = state.lastItemOrNull()?.id
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                val next = keysDao.remoteKeysById(lastId)?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                next
            }
        }

        val pageSize = state.config.pageSize
        val response: CoverLettersResponse = api.getCoverLetters(pageToLoad, pageSize, query)

        val p = response.pagination
        val currentPage = p?.currentPage ?: pageToLoad
        val lastPage    = p?.lastPage ?: currentPage
        val prevKey     = p?.previousPage
        val nextKey     = p?.nextPage

        val entities = response.coverLetters.mapNotNull { dto ->
            val id = dto.id ?: return@mapNotNull null
            val title = dto.title?.trim()
            val content = dto.content?.trim()
            val createdAt = dto.createdAt?.trim()

            // Validate fields + get normalized UI date (dd-MM-yyyy)
            val uiDate = CoverLetterValidation.validateAll(
                title = title,
                content = content,
                createdAtRaw = createdAt,
                gate = listOf(title, content)
            ) ?: return@mapNotNull null

            CoverLetterEntity(
                id = id,
                title = title!!,
                content = content!!,
                date = uiDate,            // dd-MM-yyyy from createdAt
                dateAdded = createdAt!!,  // raw createdAt for sorting
            )
        }

        db.withTransaction {
            if (loadType == LoadType.REFRESH) {
                keysDao.clearKeys()
                dao.clearAll()
            }
            if (entities.isNotEmpty()) {
                dao.upsertAll(entities)
                keysDao.insertAll(
                    entities.map { e ->
                        CoverLetterRemoteKeys(
                            coverLetterId = e.id,
                            prevKey = prevKey,
                            nextKey = nextKey
                        )
                    }
                )
            }
        }

        val endReached = nextKey == null || currentPage >= lastPage || entities.isEmpty()
        MediatorResult.Success(endOfPaginationReached = endReached)
    } catch (t: Throwable) {
        MediatorResult.Error(t)
    }
}

private fun <T : Any> PagingState<Int, T>.lastItemOrNull(): T? =
    pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
