package com.example.projectofinal.data.model

import com.google.gson.annotations.SerializedName // Opcional, si los nombres JSON no coinciden

data class UserProfile(
    // Utiliza @SerializedName si el nombre del campo en el JSON es diferente al nombre de la variable.
    // Ejemplo: @SerializedName("user_id")
    val id: String?, // O Int, Long, según lo que devuelva tu API

    val email: String?,

    // Puede que tu API use "username", "fullName", "name", etc. Ajusta según sea necesario.
    @SerializedName("name") // Ejemplo si el JSON tiene "name" pero quieres llamarlo "displayName"
    val displayName: String?, // O simplemente `val name: String?,` si coinciden

    @SerializedName("profile_photo_url") // Ejemplo
    val profilePhotoUrl: String?,

    // Añade aquí cualquier otro campo que tu API devuelva para el perfil de usuario.
    // Por ejemplo:
    // val bio: String?,
    // val location: String?,
    // val dateJoined: String?, // Considera usar un tipo de fecha adecuado si necesitas procesarla
    // val followerCount: Int?,
    // val followingCount: Int?
)