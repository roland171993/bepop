package com.stopgalere.domain.usecase.ai

import com.stopgalere.data.remote.dto.GenerateCoverLetterRequest
import com.stopgalere.domain.repository.AiRepoInterface
import javax.inject.Inject

class GenerateCoverLetterUseCase @Inject constructor(private val repo: AiRepoInterface) {
    suspend operator fun invoke(request: GenerateCoverLetterRequest): Result<String> =
        repo.generateCoverLetter(request)
}
