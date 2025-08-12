package com.example.projectofinal.data.network

import com.example.projectofinal.utils.AppConstants

object NetworkConfig {
    // URL base de la API - Mover aquí desde RetrofitInstance
    const val BASE_URL = "http://100.73.162.98:5000/api/"
    
    // Timeouts de red usando constantes centralizadas
    const val CONNECT_TIMEOUT_SECONDS = AppConstants.Timeouts.NETWORK_TIMEOUT
    const val READ_TIMEOUT_SECONDS = AppConstants.Timeouts.NETWORK_TIMEOUT
    const val WRITE_TIMEOUT_SECONDS = AppConstants.Timeouts.NETWORK_TIMEOUT
    
    // Headers comunes
    const val AUTHORIZATION_HEADER = "Authorization"
    const val CONTENT_TYPE_HEADER = "Content-Type"
    const val CONTENT_TYPE_JSON = "application/json"
    
    // Endpoints de la API
    object Endpoints {
        const val LOGIN = "auth/login"
        const val REGISTER = "auth/register"
        const val USER_PROFILE = "user/profile"
        const val UPDATE_PROFILE = "user/profile"
        const val UPLOAD_PHOTO = "user/profile/photo"
    }
}
