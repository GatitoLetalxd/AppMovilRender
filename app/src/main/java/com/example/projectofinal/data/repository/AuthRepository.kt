package com.example.projectofinal.data.repository

import com.example.projectofinal.data.model.AuthResponse
import com.example.projectofinal.data.model.LoginRequest
import com.example.projectofinal.data.model.RegisterRequest
import com.example.projectofinal.data.network.ApiService
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {

    suspend fun loginUser(loginRequest: LoginRequest): Response<AuthResponse> {
        // Aquí simplemente delegamos la llamada a la instancia de ApiService.
        // En repositorios más complejos, podrías añadir lógica aquí, como:
        // - Obtener datos de caché primero.
        // - Si falla la red, intentar obtener de caché.
        // - Combinar datos de múltiples fuentes.
        return apiService.loginUser(loginRequest)
    }

    suspend fun registerUser(registerRequest: RegisterRequest): Response<AuthResponse> {
        return apiService.registerUser(registerRequest)
    }

    // Aquí podrías agregar más funciones relacionadas con la autenticación en el futuro,
    // como forgotPassword, resetPassword, logout (si involucra una llamada a la API), etc.
}