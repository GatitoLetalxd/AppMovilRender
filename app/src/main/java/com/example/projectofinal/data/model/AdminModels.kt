package com.example.projectofinal.data.model

import com.google.gson.annotations.SerializedName

data class AdminRequest(
    @SerializedName("id_solicitud") val requestId: Int,
    @SerializedName("usuario_id") val userId: Int,
    val nombre: String,
    val correo: String,
    @SerializedName("razon") val reason: String,
    val estado: String
)

data class UserSummary(
    @SerializedName("id_usuario") val userId: Int,
    val nombre: String,
    val correo: String,
    val rol: String
)

data class AdminHandleRequest(
    val requestId: Int,
    val action: String
)


