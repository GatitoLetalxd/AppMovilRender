package com.example.projectofinal.data.model

import com.google.gson.annotations.SerializedName

// Para la respuesta de /auth/login y /auth/register

// Esta clase es para lo que devuelve /auth/login y /auth/register
data class AuthResponse(
    val token: String?,
    val user: UserDetails?, // UserDetails solo tiene id, nombre, correo, rol
    val message: String?
)
data class UserDetails(
    val id: Int?,
    @SerializedName("nombre")
    val nombre: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("rol")
    val rol: String?
    // foto_perfil y fecha_registro NO están aquí
)
