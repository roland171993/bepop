package com.stopgalere.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.stopgalere.data.local.AppDatabase
import com.stopgalere.data.model.CoverLetterEntity
import com.stopgalere.data.model.toDomain
import com.stopgalere.data.remote.ApiService
import com.stopgalere.domain.model.CoverLetter
import com.stopgalere.domain.repository.CoverLetterRepoInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class)
@Singleton
class CoverLetterRepository @Inject constructor(
    private val db: AppDatabase,
    private val api: ApiService
) : CoverLetterRepoInterface {

    private val dao = db.coverLetterDao()

    override fun getCoverLetters(query: String?, online: Boolean): Flow<PagingData<CoverLetter>> {
        val pageSize = 15
        val config = PagingConfig(
            pageSize = pageSize,
            initialLoadSize = pageSize,
            prefetchDistance = 3,
            enablePlaceholders = true
        )

        return if (online) {
            Pager(
                config = config,
                remoteMediator = CoverLetterRemoteMediator(db, api, query),
                pagingSourceFactory = { dao.pagingSource(query) }
            ).flow.map { it.map(CoverLetterEntity::toDomain) }
        } else {
            Pager(
                config = config,
                pagingSourceFactory = { dao.pagingSource(query) }
            ).flow.map { it.map(CoverLetterEntity::toDomain) }
        }
    }

    override fun coverLetterById(id: String): Flow<CoverLetter?> =
        dao.observeById(id).map { it?.toDomain() }
}
