package com.example.projectofinal.data.model

import com.google.gson.annotations.SerializedName

data class UserProfileUpdateRequest(
    // El backend espera el campo JSON como "nombre"
    @SerializedName("nombre")
    val nombre: String?, // El usuario puede querer actualizar su nombre

    // Otros campos opcionales que el usuario podría actualizar:
    // @SerializedName("bio") // Si tienes un campo para la biografía
    // val bio: String?,

    // @SerializedName("location") // Si tienes un campo para la ubicación
    // val location: String?,

    // Considera si el cambio de correo electrónico y contraseña se manejan aquí o
    // a través de endpoints dedicados.
    // Si permites cambiar el correo (asegúrate de la verificación en el backend):
    // @SerializedName("correo")
    // val correo: String?,

    // Si permites cambiar la contraseña DESDE la pantalla de perfil (menos común sin la actual):
    // @SerializedName("new_password") // O el nombre que espere tu backend
    // val nuevaContrasena: String?

    // Importante: No incluyas 'id', 'rol', 'fecha_registro' o 'foto_perfil' aquí.
    // La foto de perfil se maneja a través de un endpoint de carga Multipart separado.
)