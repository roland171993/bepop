package com.stopgalere.domain.usecase.auth

import com.stopgalere.data.remote.dto.AuthResponse
import com.stopgalere.domain.repository.AuthRepoInterface
import javax.inject.Inject

/** Validates inputs then delegates login to the repository. */
class LoginUseCase @Inject constructor(
    private val repo: AuthRepoInterface
) {
    suspend operator fun invoke(email: String, password: String): AuthResponse {
        require(email.isNotBlank())    { "Email must not be blank." }
        require(password.isNotBlank()) { "Password must not be blank." }
        return repo.login(email.trim(), password)
    }
}
