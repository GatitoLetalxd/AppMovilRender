package com.example.projectofinal.data.network

import com.example.projectofinal.data.model.AuthResponse
import com.example.projectofinal.data.model.GenericResponse
import com.example.projectofinal.data.model.LoginRequest
import com.example.projectofinal.data.model.RegisterRequest
import com.example.projectofinal.data.model.UserDetails
import com.example.projectofinal.data.model.UserProfile
import com.example.projectofinal.data.model.UserProfileData
import com.example.projectofinal.data.model.UserProfileUpdateRequest
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface ApiService {

    @POST(NetworkConfig.Endpoints.LOGIN)
    suspend fun loginUser(
        @Body loginRequest: LoginRequest
    ): Response<AuthResponse>

    @POST(NetworkConfig.Endpoints.REGISTER)
    suspend fun registerUser(
        @Body registerRequest: RegisterRequest
    ): Response<AuthResponse>

    // Endpoints de Usuario (Perfil)
    @GET(NetworkConfig.Endpoints.USER_PROFILE)
    suspend fun getUserProfile(
        @Header(NetworkConfig.AUTHORIZATION_HEADER) token: String
    ): Response<UserProfileData>

    @PUT(NetworkConfig.Endpoints.UPDATE_PROFILE)
    suspend fun updateUserProfile(
        @Header(NetworkConfig.AUTHORIZATION_HEADER) token: String,
        @Body profileData: UserProfileUpdateRequest
    ): Response<UserProfileData>

    @Multipart
    @POST(NetworkConfig.Endpoints.UPLOAD_PHOTO)
    suspend fun uploadProfilePhoto(
        @Header(NetworkConfig.AUTHORIZATION_HEADER) token: String,
        @Part photo: MultipartBody.Part
    ): Response<GenericResponse>
}

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