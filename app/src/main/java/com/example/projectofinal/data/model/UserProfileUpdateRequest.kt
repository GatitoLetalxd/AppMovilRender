package com.example.projectofinal.data.model

import com.google.gson.annotations.SerializedName

data class UserProfileUpdateRequest(
    // El backend espera el campo JSON como "nombre"
    @SerializedName("nombre")
    val nombre: String?, // El usuario puede querer actualizar su nombre

    // El backend para actualizar perfil espera "correo"
    @SerializedName("correo")
    val correo: String?

)