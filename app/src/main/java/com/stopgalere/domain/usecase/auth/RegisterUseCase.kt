package com.stopgalere.domain.usecase.auth

import com.stopgalere.data.remote.dto.AuthResponse
import com.stopgalere.domain.repository.AuthRepoInterface
import javax.inject.Inject

/** Validates registration inputs then delegates to the repository. */
class RegisterUseCase @Inject constructor(
    private val repo: AuthRepoInterface
) {
    suspend operator fun invoke(
        firstName: String,
        lastName:  String,
        age:       Int?,
        email:     String,
        phone:     String,
        password:  String
    ): AuthResponse {
        require(email.isNotBlank())    { "Email must not be blank." }
        require(password.length >= 8)  { "Password must be at least 8 characters." }
        return repo.register(
            firstName.trim(), lastName.trim(), age,
            email.trim(), phone.trim(), password
        )
    }
}
