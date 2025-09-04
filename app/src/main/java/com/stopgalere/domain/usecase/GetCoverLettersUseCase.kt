package com.stopgalere.domain.usecase

import androidx.paging.PagingData
import com.stopgalere.domain.model.CoverLetter
import com.stopgalere.domain.repository.CoverLetterRepoInterface
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCoverLettersUseCase @Inject constructor(
    private val repo: CoverLetterRepoInterface
) {
    operator fun invoke(query: String?, online: Boolean): Flow<PagingData<CoverLetter>> =
        repo.getCoverLetters(query, online)
}
