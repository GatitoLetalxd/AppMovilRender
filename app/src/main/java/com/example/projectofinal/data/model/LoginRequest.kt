package com.example.projectofinal.data.model

data class LoginRequest(
    val correo: String, // O el campo que use tu API para el identificador, ej: username
    val contraseña: String
)