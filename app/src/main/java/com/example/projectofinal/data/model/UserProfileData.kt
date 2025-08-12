package com.example.projectofinal.data.model


import com.google.gson.annotations.SerializedName

// Para la respuesta de /api/user/profile
data class UserProfileData( // Nombre diferente para evitar confusión
    val id: Int?, // o String?, asegúrate que coincida con el tipo que envía el backend para este endpoint
    @SerializedName("nombre")
    val nombre: String?,
    @SerializedName("correo")
    val correo: String?,
    @SerializedName("rol")
    val rol: String?,
    @SerializedName("foto_perfil") // Campo del JSON es "foto_perfil"
    val fotoPerfilUrl: String?,    // Tu variable en Kotlin
    @SerializedName("fecha_registro") // Campo del JSON es "fecha_registro"
    val fechaRegistro: String?        // Tu variable en Kotlin
    // Puedes añadir más campos si el endpoint de perfil los devuelve
)