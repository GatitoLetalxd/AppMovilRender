package com.example.projectofinal.data.model

data class RegisterRequest(
    val nombre: String,
    val email: String,
    val password: String
    // Podrías añadir campos como 'confirmPassword' si tu UI lo maneja,
    // pero solo envía a la API lo que espera.
)