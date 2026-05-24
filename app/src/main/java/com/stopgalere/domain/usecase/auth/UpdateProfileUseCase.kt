package com.stopgalere.domain.usecase.auth

import com.stopgalere.data.remote.dto.UpdateProfileRequest
import com.stopgalere.data.remote.dto.UserDto
import com.stopgalere.domain.repository.AuthRepoInterface
import javax.inject.Inject

/** Persists editable profile fields on the backend. */
class UpdateProfileUseCase @Inject constructor(
    private val repo: AuthRepoInterface
) {
    suspend operator fun invoke(
        firstName: String?,
        lastName:  String?,
        age:       Int?,
        phone:     String?
    ): UserDto = repo.updateProfile(
        UpdateProfileRequest(
            firstName = firstName?.trim(),
            lastName  = lastName?.trim(),
            age       = age,
            phone     = phone?.trim()
        )
    )
}
