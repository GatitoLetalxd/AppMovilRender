package com.example.projectofinal.data.repository

import com.example.projectofinal.data.datastore.UserPreferencesRepository
import com.example.projectofinal.data.model.GenericResponse
import com.example.projectofinal.data.model.UserProfileData // <--- CAMBIO IMPORTANTE AQUÍ
import com.example.projectofinal.data.model.UserProfileUpdateRequest
import com.example.projectofinal.data.network.ApiService
import kotlinx.coroutines.flow.firstOrNull
import okhttp3.MultipartBody
import java.io.IOException

class UserRepository(
    private val apiService: ApiService,
    private val userPreferencesRepository: UserPreferencesRepository
) {

    suspend fun getUserProfile(): Result<UserProfileData> { // <--- CAMBIO IMPORTANTE AQUÍ
        val token = userPreferencesRepository.authToken.firstOrNull()
        if (token.isNullOrBlank()) {
            return Result.failure(NotAuthenticatedException("User not authenticated or token is missing."))
        }
        return try {
            // Asumimos que apiService.getUserProfile() ahora devuelve Response<UserProfileData>
            val response = apiService.getUserProfile("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error fetching profile"
                Result.failure(ApiException("Error ${response.code()}: $errorBody"))
            }
        } catch (e: IOException) {
            Result.failure(NetworkException("Network error fetching profile: ${e.message}", e))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error fetching profile: ${e.message}", e))
        }
    }

    suspend fun updateUserProfile(profileData: UserProfileUpdateRequest): Result<UserProfileData> { // <--- CAMBIO IMPORTANTE AQUÍ
        val token = userPreferencesRepository.authToken.firstOrNull()
        if (token.isNullOrBlank()) {
            return Result.failure(NotAuthenticatedException("User not authenticated or token is missing."))
        }
        return try {
            // Asumimos que apiService.updateUserProfile() ahora devuelve Response<UserProfileData>
            // Si devuelve otra cosa (ej. GenericResponse), ajusta esto.
            val response = apiService.updateUserProfile("Bearer $token", profileData)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error updating profile"
                Result.failure(ApiException("Error ${response.code()}: $errorBody"))
            }
        } catch (e: IOException) {
            Result.failure(NetworkException("Network error updating profile: ${e.message}", e))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error updating profile: ${e.message}", e))
        }
    }

    suspend fun uploadProfilePhoto(photo: MultipartBody.Part): Result<GenericResponse> {
        val token = userPreferencesRepository.authToken.firstOrNull()
        if (token.isNullOrBlank()) {
            return Result.failure(NotAuthenticatedException("User not authenticated or token is missing."))
        }
        return try {
            val response = apiService.uploadProfilePhoto("Bearer $token", photo)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error uploading photo"
                Result.failure(ApiException("Error ${response.code()}: $errorBody"))
            }
        } catch (e: IOException) {
            Result.failure(NetworkException("Network error uploading photo: ${e.message}", e))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error uploading photo: ${e.message}", e))
        }
    }
}

// Clases de excepción personalizadas (sin cambios, ya estaban bien)
class ApiException(message: String) : Exception(message)
class NetworkException(message: String, cause: Throwable? = null) : IOException(message, cause)
class NotAuthenticatedException(message: String) : Exception(message)
