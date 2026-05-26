package com.arenamix.app.data

import retrofit2.http.Body
import retrofit2.http.POST

// ── DTOs que coinciden con lo que devuelve el backend ────────────────────────

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val username: String,
    val rol: String?,
    val usuarioId: Int?
)

// ── Interfaz Retrofit ─────────────────────────────────────────────────────────

interface ApiService {

    @POST("/api/auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @POST("/api/auth/registro")
    suspend fun registro(@Body body: RegisterRequest): AuthResponse
}
