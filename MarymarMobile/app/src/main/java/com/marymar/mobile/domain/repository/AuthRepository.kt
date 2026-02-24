package com.marymar.mobile.domain.repository

import com.marymar.mobile.core.util.ApiResult
import com.marymar.mobile.domain.model.Role
import com.marymar.mobile.domain.model.Session

interface AuthRepository {
    suspend fun register(
        idNumber: String,
        name: String,
        email: String,
        password: String,
        phone: String,
        birthDateIso: String,
        role: Role
    ): ApiResult<Session>

    /**
     * Login inicia 2FA en tu backend: devuelve requires2FA=true y token=null.
     */
    suspend fun login(email: String, password: String): ApiResult<LoginStep>

    suspend fun validateCode(email: String, code: String): ApiResult<Session>

    suspend fun logout()
}

data class LoginStep(
    val requires2FA: Boolean,
    val message: String?
)
