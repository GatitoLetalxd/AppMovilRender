package com.example.projectofinal.utils

object AppConstants {
    
    // Timeouts y delays
    object Timeouts {
        const val NETWORK_TIMEOUT = 30L
        const val SPLASH_DELAY = 2000L
        const val SNACKBAR_DURATION_SHORT = 3000L
        const val SNACKBAR_DURATION_LONG = 5000L
    }
    
    // Validaciones
    object Validation {
        const val MIN_PASSWORD_LENGTH = 6
        const val MIN_USERNAME_LENGTH = 3
        const val MAX_USERNAME_LENGTH = 50
        const val MAX_EMAIL_LENGTH = 100
    }
    
    // Mensajes de error comunes
    object ErrorMessages {
        const val NETWORK_ERROR = "Error de red. Verifica tu conexión."
        const val UNKNOWN_ERROR = "Ocurrió un error inesperado."
        const val INCOMPLETE_RESPONSE = "Respuesta incompleta del servidor."
        const val INVALID_CREDENTIALS = "Credenciales inválidas."
        const val SERVER_ERROR = "Error del servidor. Intenta más tarde."
    }
    
    // Mensajes de éxito
    object SuccessMessages {
        const val LOGIN_SUCCESS = "Login exitoso. ¡Bienvenido!"
        const val REGISTER_SUCCESS = "Registro exitoso. ¡Bienvenido!"
        const val LOGOUT_SUCCESS = "Sesión cerrada exitosamente."
        const val PROFILE_UPDATED = "Perfil actualizado exitosamente."
    }
    
    // Nombres de archivos y directorios
    object FileNames {
        const val USER_PREFERENCES = "user_preferences"
        const val HTTP_CACHE = "http_cache"
        const val PROFILE_PHOTO = "profile_photo"
    }
    
    // Tamaños de archivo
    object FileSizes {
        const val MAX_PHOTO_SIZE = 5 * 1024 * 1024 // 5MB
        const val HTTP_CACHE_SIZE = 10 * 1024 * 1024 // 10MB
    }
}
