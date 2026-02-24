package com.marymar.mobile.domain.usecase

import com.marymar.mobile.domain.model.Role
import com.marymar.mobile.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) = repo.login(email, password)
}

class ValidateCodeUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, code: String) = repo.validateCode(email, code)
}

class RegisterUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(
        idNumber: String,
        name: String,
        email: String,
        password: String,
        phone: String,
        birthDateIso: String,
        role: Role
    ) = repo.register(idNumber, name, email, password, phone, birthDateIso, role)
}

class LogoutUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke() = repo.logout()
}
