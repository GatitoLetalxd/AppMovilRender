package com.example.projectofinal.data.model

data class RegisterRequest(
    val nombre: String, // O los campos que necesite tu API para registrar
    val correo: String,
    val contraseña: String
    // Podrías añadir campos como 'confirmPassword' si tu UI lo maneja,
    // pero solo envía a la API lo que espera.
)