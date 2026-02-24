package com.marymar.mobile.data.repository

import com.marymar.mobile.core.network.TokenProvider
import com.marymar.mobile.core.storage.SessionStore
import com.marymar.mobile.core.util.ApiResult
import com.marymar.mobile.data.remote.api.AuthApi
import com.marymar.mobile.data.remote.dto.*
import com.marymar.mobile.domain.model.Role
import com.marymar.mobile.domain.model.Session
import com.marymar.mobile.domain.repository.AuthRepository
import com.marymar.mobile.domain.repository.LoginStep
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val sessionStore: SessionStore,
    private val tokenProvider: TokenProvider
) : AuthRepository {

    override suspend fun register(
        idNumber: String,
        name: String,
        email: String,
        password: String,
        phone: String,
        birthDateIso: String,
        role: Role
    ): ApiResult<Session> {
        return try {
            val resp = api.register(
                RegisterRequestDto(
                    numeroIdentificacion = idNumber,
                    nombre = name,
                    email = email,
                    contrasena = password,
                    telefono = phone,
                    fechaNacimiento = birthDateIso,
                    rol = role.name
                )
            )

            val token = resp.token
                ?: return ApiResult.Error(resp.mensaje ?: "No se recibió token en el registro")

            // Con tu backend, el token no trae el id, entonces validamos con verify-token.
            val persona = api.verifyToken(VerifyTokenRequestDto(token))

            val session = Session(
                token = token,
                userId = persona.id,
                email = persona.email,
                name = resp.nombre ?: persona.nombre,
                role = Role.valueOf(persona.rol)
            )

            sessionStore.saveSession(
                token = session.token,
                email = session.email,
                name = session.name,
                role = session.role.name,
                userId = session.userId
            )
            tokenProvider.setToken(session.token)

            ApiResult.Success(session)

        } catch (e: HttpException) {
            ApiResult.Error("Error HTTP ${e.code()}", e.code(), e)
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error inesperado", null, e)
        }
    }

    override suspend fun login(email: String, password: String): ApiResult<LoginStep> {
        return try {
            val resp = api.login(LoginRequestDto(email = email, contrasena = password))
            ApiResult.Success(
                LoginStep(
                    requires2FA = resp.requires2FA ?: false,
                    message = resp.mensaje
                )
            )
        } catch (e: HttpException) {
            ApiResult.Error("Credenciales inválidas o error HTTP ${e.code()}", e.code(), e)
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error inesperado", null, e)
        }
    }

    override suspend fun validateCode(email: String, code: String): ApiResult<Session> {
        return try {
            val resp = api.validateCode(email = email, code = code)

            val token = resp.token
                ?: return ApiResult.Error(resp.mensaje ?: "Código inválido o sin token")

            val persona = api.verifyToken(VerifyTokenRequestDto(token))

            val session = Session(
                token = token,
                userId = persona.id,
                email = persona.email,
                name = resp.nombre ?: persona.nombre,
                role = Role.valueOf(persona.rol)
            )

            sessionStore.saveSession(
                token = session.token,
                email = session.email,
                name = session.name,
                role = session.role.name,
                userId = session.userId
            )
            tokenProvider.setToken(session.token)

            ApiResult.Success(session)

        } catch (e: HttpException) {
            ApiResult.Error("Error HTTP ${e.code()}", e.code(), e)
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error inesperado", null, e)
        }
    }

    override suspend fun logout() {
        tokenProvider.setToken(null)
        sessionStore.clear()
    }
}
