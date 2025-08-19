package com.stopgalere.domain.usecase

import androidx.paging.PagingData
import com.stopgalere.domain.model.Job
import com.stopgalere.domain.repository.JobRepoInterface
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetJobsUseCase @Inject constructor(
    private val repo: JobRepoInterface
) {
    operator fun invoke(query: String?, online: Boolean): Flow<PagingData<Job>> =
        repo.getJobs(query, online)
}
