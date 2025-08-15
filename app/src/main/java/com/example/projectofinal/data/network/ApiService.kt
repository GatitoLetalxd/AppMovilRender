package com.example.projectofinal.data.network

import com.example.projectofinal.data.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- Autenticación ---
    @POST(NetworkConfig.Endpoints.LOGIN)
    suspend fun loginUser(
        @Body loginRequest: LoginRequest
    ): Response<AuthResponse>

    @POST(NetworkConfig.Endpoints.REGISTER)
    suspend fun registerUser(
        @Body registerRequest: RegisterRequest
    ): Response<AuthResponse>

    // --- Perfil de Usuario ---
    @GET(NetworkConfig.Endpoints.USER_PROFILE)
    suspend fun getUserProfile(
        @Header(NetworkConfig.AUTHORIZATION_HEADER) token: String
    ): Response<UserProfileData>

    @PUT(NetworkConfig.Endpoints.UPDATE_PROFILE)
    suspend fun updateUserProfile(
        @Header(NetworkConfig.AUTHORIZATION_HEADER) token: String,
        @Body profileData: UserProfileUpdateRequest
    ): Response<UpdateProfileResponse>

    @Multipart
    @POST(NetworkConfig.Endpoints.UPLOAD_PHOTO)
    suspend fun uploadProfilePhoto(
        @Header(NetworkConfig.AUTHORIZATION_HEADER) token: String,
        @Part photo: MultipartBody.Part
    ): Response<GenericResponse>

    // --- Amigos y Búsqueda ---
    @GET(NetworkConfig.Endpoints.SEARCH_USERS)
    suspend fun searchUsers(
        @Header(NetworkConfig.AUTHORIZATION_HEADER) token: String,
        @Query("query") query: String
    ): Response<List<UserSearchResult>>

    @GET(NetworkConfig.Endpoints.FRIEND_LIST)
    suspend fun getFriends(
        @Header(NetworkConfig.AUTHORIZATION_HEADER) token: String
    ): Response<List<Friend>>

    @GET(NetworkConfig.Endpoints.PENDING_REQUESTS)
    suspend fun getPendingFriendRequests(
        @Header(NetworkConfig.AUTHORIZATION_HEADER) token: String
    ): Response<List<FriendRequest>>

    @POST(NetworkConfig.Endpoints.SEND_REQUEST)
    suspend fun sendFriendRequest(
        @Header(NetworkConfig.AUTHORIZATION_HEADER) token: String,
        @Path("friendId") friendId: Int
    ): Response<GenericResponse>

    @POST(NetworkConfig.Endpoints.ACCEPT_REQUEST)
    suspend fun acceptFriendRequest(
        @Header(NetworkConfig.AUTHORIZATION_HEADER) token: String,
        @Path("friendId") friendId: Int
    ): Response<GenericResponse>

    @POST(NetworkConfig.Endpoints.REJECT_REQUEST)
    suspend fun rejectFriendRequest(
        @Header(NetworkConfig.AUTHORIZATION_HEADER) token: String,
        @Path("friendId") friendId: Int
    ): Response<GenericResponse>

    // --- Administración de roles ---
    @POST(NetworkConfig.Endpoints.ADMIN_REQUEST)
    suspend fun requestAdminRole(
        @Header(NetworkConfig.AUTHORIZATION_HEADER) token: String,
        @Body body: Map<String, String> // { reason }
    ): Response<GenericResponse>

    @GET(NetworkConfig.Endpoints.ADMIN_PENDING)
    suspend fun getAdminRequests(
        @Header(NetworkConfig.AUTHORIZATION_HEADER) token: String
    ): Response<List<AdminRequest>>

    @PUT(NetworkConfig.Endpoints.ADMIN_HANDLE)
    suspend fun decideAdminRequest(
        @Header(NetworkConfig.AUTHORIZATION_HEADER) token: String,
        @Body body: AdminHandleRequest
    ): Response<GenericResponse>

    @GET(NetworkConfig.Endpoints.USERS_LIST)
    suspend fun getAllUsers(
        @Header(NetworkConfig.AUTHORIZATION_HEADER) token: String
    ): Response<List<UserSummary>>

    @DELETE(NetworkConfig.Endpoints.ADMIN_REMOVE)
    suspend fun demoteAdmin(
        @Header(NetworkConfig.AUTHORIZATION_HEADER) token: String,
        @Path("adminId") adminId: String
    ): Response<GenericResponse>

    // --- Gestión de Imágenes ---
    @Multipart
    @POST(NetworkConfig.Endpoints.UPLOAD_IMAGE)
    suspend fun uploadImage(
        @Header(NetworkConfig.AUTHORIZATION_HEADER) token: String,
        @Part image: MultipartBody.Part
    ): Response<ImageUploadResponse>

    @GET(NetworkConfig.Endpoints.GET_USER_IMAGES)
    suspend fun getUserImages(
        @Header(NetworkConfig.AUTHORIZATION_HEADER) token: String
    ): Response<List<UserImage>>

    @POST(NetworkConfig.Endpoints.PROCESS_IMAGE)
    suspend fun processImage(
        @Header(NetworkConfig.AUTHORIZATION_HEADER) token: String,
        @Path("imageId") imageId: Int
    ): Response<ProcessImageResponse>

    @GET(NetworkConfig.Endpoints.DOWNLOAD_IMAGE)
    suspend fun downloadProcessedImage(
        @Header(NetworkConfig.AUTHORIZATION_HEADER) token: String,
        @Path("imageId") imageId: Int
    ): Response<okhttp3.ResponseBody>

    @DELETE(NetworkConfig.Endpoints.DELETE_IMAGE)
    suspend fun deleteImage(
        @Header(NetworkConfig.AUTHORIZATION_HEADER) token: String,
        @Path("imageId") imageId: Int
    ): Response<GenericImageResponse>
}
