package com.stopgalere.domain.usecase.auth

import com.stopgalere.data.remote.dto.UserDto
import com.stopgalere.domain.repository.AuthRepoInterface
import javax.inject.Inject

/** Fetches the current user's profile from the backend. */
class GetProfileUseCase @Inject constructor(
    private val repo: AuthRepoInterface
) {
    suspend operator fun invoke(): UserDto = repo.getProfile()
}
