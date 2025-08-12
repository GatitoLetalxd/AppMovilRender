package com.example.projectofinal.data.model

import com.google.gson.annotations.SerializedName

data class Friend(
    @SerializedName("id_usuario")
    val userId: Int,
    val nombre: String,
    @SerializedName("foto_perfil")
    val profilePictureUrl: String?
)

data class FriendRequest(
    @SerializedName("id_usuario")
    val userId: Int,
    val nombre: String,
    @SerializedName("foto_perfil")
    val profilePictureUrl: String?
)

data class UserSearchResult(
    @SerializedName("id_usuario")
    val userId: Int,
    val nombre: String,
    @SerializedName("foto_perfil")
    val profilePictureUrl: String?,
    val estado: String?
)
