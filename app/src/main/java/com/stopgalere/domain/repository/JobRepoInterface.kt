package com.stopgalere.domain.repository

import androidx.paging.PagingData
import com.stopgalere.domain.model.Job
import kotlinx.coroutines.flow.Flow

interface JobRepoInterface {
    /** Offline-first paged jobs, optionally filtered by a search query. */
    fun pagedJobs(query: String?): Flow<PagingData<Job>>
}
