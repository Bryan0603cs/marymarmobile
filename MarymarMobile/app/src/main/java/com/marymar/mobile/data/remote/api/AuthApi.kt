package com.marymar.mobile.data.remote.api

import com.marymar.mobile.data.remote.dto.*
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequestDto): AuthResponseDto

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequestDto): AuthResponseDto

    @POST("api/auth/validate-code")
    suspend fun validateCode(
        @Query("email") email: String,
        @Query("code") code: String
    ): AuthResponseDto

    @POST("api/auth/verify-token")
    suspend fun verifyToken(@Body body: VerifyTokenRequestDto): PersonaResponseDto
}
