package com.example.projectofinal.data.repository

import com.example.projectofinal.data.model.*
import com.example.projectofinal.data.network.ApiService
import okhttp3.MultipartBody
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {

    // --- Autenticación ---
    suspend fun loginUser(loginRequest: LoginRequest): Response<AuthResponse> {
        return apiService.loginUser(loginRequest)
    }

    suspend fun registerUser(registerRequest: RegisterRequest): Response<AuthResponse> {
        return apiService.registerUser(registerRequest)
    }

    // --- Perfil de Usuario ---
    suspend fun getUserProfile(token: String): Response<UserProfileData> {
        return apiService.getUserProfile(token)
    }

    suspend fun updateUserProfile(token: String, profileData: UserProfileUpdateRequest): Response<UpdateProfileResponse> {
        return apiService.updateUserProfile(token, profileData)
    }

    suspend fun uploadProfilePhoto(token: String, photo: MultipartBody.Part): Response<GenericResponse> {
        return apiService.uploadProfilePhoto(token, photo)
    }

    // --- Amigos y Búsqueda ---
    suspend fun searchUsers(token: String, query: String): Response<List<UserSearchResult>> {
        return apiService.searchUsers(token, query)
    }

    suspend fun getFriends(token: String): Response<List<Friend>> {
        return apiService.getFriends(token)
    }

    suspend fun getPendingFriendRequests(token: String): Response<List<FriendRequest>> {
        return apiService.getPendingFriendRequests(token)
    }

    suspend fun sendFriendRequest(token: String, friendId: Int): Response<GenericResponse> {
        return apiService.sendFriendRequest(token, friendId)
    }

    suspend fun acceptFriendRequest(token: String, friendId: Int): Response<GenericResponse> {
        return apiService.acceptFriendRequest(token, friendId)
    }

    suspend fun rejectFriendRequest(token: String, friendId: Int): Response<GenericResponse> {
        return apiService.rejectFriendRequest(token, friendId)
    }
}
