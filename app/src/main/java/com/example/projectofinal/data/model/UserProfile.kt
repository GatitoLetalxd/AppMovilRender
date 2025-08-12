package com.example.projectofinal.data.model

import com.google.gson.annotations.SerializedName

data class UserProfile(
    val id: Int?,
    val nombre: String?,
    val correo: String?,
    val rol: String?,
    @SerializedName("foto_perfil")
    val fotoPerfilUrl: String?,
    @SerializedName("fecha_registro")
    val fechaRegistro: String?
)
