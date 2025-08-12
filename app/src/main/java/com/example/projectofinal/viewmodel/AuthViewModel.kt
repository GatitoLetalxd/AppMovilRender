package com.example.projectofinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectofinal.data.datastore.UserPreferencesRepository
import com.example.projectofinal.data.model.Friend
import com.example.projectofinal.data.model.FriendRequest
import com.example.projectofinal.data.model.LoginRequest
import com.example.projectofinal.data.model.RegisterRequest
import com.example.projectofinal.data.model.UserProfile
import com.example.projectofinal.data.model.UserProfileData
import com.example.projectofinal.data.model.UserSearchResult
import com.example.projectofinal.data.repository.AuthRepository
import com.example.projectofinal.data.model.UserProfileUpdateRequest
import com.example.projectofinal.ui.uistate.AuthUiState
import com.example.projectofinal.ui.uistate.FriendRequestsUiState
import com.example.projectofinal.ui.uistate.FriendsUiState
import com.example.projectofinal.ui.uistate.ProfileUiState
import com.example.projectofinal.ui.uistate.SearchUiState
import okhttp3.MultipartBody
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val authTokenStream = userPreferencesRepository.authToken
    val currentDisplayName = userPreferencesRepository.displayName

    private val _registerUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val registerUiState: StateFlow<AuthUiState> = _registerUiState.asStateFlow()

    private val _loginUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val loginUiState: StateFlow<AuthUiState> = _loginUiState.asStateFlow()

    private val _profileUiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val profileUiState: StateFlow<ProfileUiState> = _profileUiState.asStateFlow()

    private val _friendsUiState = MutableStateFlow<FriendsUiState>(FriendsUiState.Loading)
    val friendsUiState: StateFlow<FriendsUiState> = _friendsUiState.asStateFlow()

    private val _friendRequestsUiState = MutableStateFlow<FriendRequestsUiState>(FriendRequestsUiState.Loading)
    val friendRequestsUiState: StateFlow<FriendRequestsUiState> = _friendRequestsUiState.asStateFlow()

    private val _searchUiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val searchUiState: StateFlow<SearchUiState> = _searchUiState.asStateFlow()

    fun registerUser(request: RegisterRequest) {
        viewModelScope.launch {
            _registerUiState.value = AuthUiState.Loading
            try {
                val response = authRepository.registerUser(request)
                if (response.isSuccessful && response.body() != null) {
                    _registerUiState.value = AuthUiState.Success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    _registerUiState.value = AuthUiState.Error(errorBody)
                }
            } catch (e: Exception) {
                _registerUiState.value = AuthUiState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun loginUser(request: LoginRequest) {
        viewModelScope.launch {
            _loginUiState.value = AuthUiState.Loading
            try {
                val response = authRepository.loginUser(request)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val token = body.token ?: ""
                    val userId = body.user?.id?.toString() ?: ""
                    val displayName = body.user?.nombre ?: ""
                    val email = body.user?.email
                    if (token.isNotBlank()) {
                        userPreferencesRepository.saveAuthDetails(
                            token = token,
                            userId = userId,
                            displayName = displayName,
                            email = email
                        )
                    }
                    _loginUiState.value = AuthUiState.Success(body)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    _loginUiState.value = AuthUiState.Error(errorBody)
                }
            } catch (e: Exception) {
                _loginUiState.value = AuthUiState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun loginUser(email: String, password: String) {
        // Backend espera 'correo' y 'contraseña'
        loginUser(LoginRequest(correo = email, contrasena = password))
    }

    fun registerUser(nombre: String, correo: String, contraseña: String) {
        registerUser(RegisterRequest(nombre = nombre, email = correo, password = contraseña))
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            _profileUiState.value = ProfileUiState.Loading
            try {
                val token = userPreferencesRepository.authToken.firstOrNull()
                if (token.isNullOrBlank()) {
                    _profileUiState.value = ProfileUiState.Error("Token no disponible")
                    return@launch
                }
                val response = authRepository.getUserProfile("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val data: UserProfileData = response.body()!!
                    val mapped = UserProfile(
                        id = data.id,
                        nombre = data.nombre,
                        correo = data.correo,
                        rol = data.rol,
                        fotoPerfilUrl = data.fotoPerfilUrl,
                        fechaRegistro = data.fechaRegistro
                    )
                    _profileUiState.value = ProfileUiState.Success(mapped)
                } else {
                    _profileUiState.value = ProfileUiState.Error("Error al cargar el perfil: ${response.message()}")
                }
            } catch (e: Exception) {
                _profileUiState.value = ProfileUiState.Error("Excepción: ${e.message}")
            }
        }
    }

    fun getFriendsList() {
        viewModelScope.launch {
            _friendsUiState.value = FriendsUiState.Loading
            try {
                val token = userPreferencesRepository.authToken.firstOrNull()
                if (token.isNullOrBlank()) {
                    _friendsUiState.value = FriendsUiState.Error("Token no disponible")
                    return@launch
                }
                val response = authRepository.getFriends("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    _friendsUiState.value = FriendsUiState.Success(response.body()!!)
                } else {
                    _friendsUiState.value = FriendsUiState.Error("Error al obtener amigos")
                }
            } catch (e: Exception) {
                _friendsUiState.value = FriendsUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun getPendingRequests() {
        viewModelScope.launch {
            _friendRequestsUiState.value = FriendRequestsUiState.Loading
            try {
                val token = userPreferencesRepository.authToken.firstOrNull()
                if (token.isNullOrBlank()) {
                    _friendRequestsUiState.value = FriendRequestsUiState.Error("Token no disponible")
                    return@launch
                }
                val response = authRepository.getPendingFriendRequests("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    _friendRequestsUiState.value = FriendRequestsUiState.Success(response.body()!!)
                } else {
                    _friendRequestsUiState.value = FriendRequestsUiState.Error("Error al obtener solicitudes")
                }
            } catch (e: Exception) {
                _friendRequestsUiState.value = FriendRequestsUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun searchUsers(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _searchUiState.value = SearchUiState.Idle
                return@launch
            }
            _searchUiState.value = SearchUiState.Loading
            try {
                val token = userPreferencesRepository.authToken.firstOrNull()
                if (token.isNullOrBlank()) {
                    _searchUiState.value = SearchUiState.Error("Token no disponible")
                    return@launch
                }
                val response = authRepository.searchUsers("Bearer $token", query)
                if (response.isSuccessful && response.body() != null) {
                    _searchUiState.value = SearchUiState.Success(response.body()!!)
                } else {
                    _searchUiState.value = SearchUiState.Error("Error en la búsqueda")
                }
            } catch (e: Exception) {
                _searchUiState.value = SearchUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun sendFriendRequest(userId: Int) {
        viewModelScope.launch {
            try {
                val token = userPreferencesRepository.authToken.firstOrNull() ?: return@launch
                authRepository.sendFriendRequest("Bearer $token", userId)
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    fun respondToFriendRequest(requestId: Int, accepted: Boolean) {
        viewModelScope.launch {
            try {
                val token = userPreferencesRepository.authToken.firstOrNull() ?: return@launch
                if (accepted) {
                    authRepository.acceptFriendRequest("Bearer $token", requestId)
                } else {
                    authRepository.rejectFriendRequest("Bearer $token", requestId)
                }
                getPendingRequests()
                if(accepted) getFriendsList()
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }
    
    fun resetLoginState() {
        _loginUiState.value = AuthUiState.Idle
    }

    fun resetRegisterState() {
        _registerUiState.value = AuthUiState.Idle
    }

    fun logoutUser() {
        viewModelScope.launch {
            userPreferencesRepository.clearAuthCredentials()
        }
    }

    private fun mapProfileDataToDomain(data: UserProfileData): UserProfile =
        UserProfile(
            id = data.id,
            nombre = data.nombre,
            correo = data.correo,
            rol = data.rol,
            fotoPerfilUrl = data.fotoPerfilUrl,
            fechaRegistro = data.fechaRegistro
        )

    fun updateUserProfile(nombre: String, correo: String) {
        viewModelScope.launch {
            _profileUiState.value = ProfileUiState.Loading
            try {
                val token = userPreferencesRepository.authToken.firstOrNull()
                if (token.isNullOrBlank()) {
                    _profileUiState.value = ProfileUiState.Error("Token no disponible")
                    return@launch
                }
                val response = authRepository.updateUserProfile(
                    token = "Bearer $token",
                    profileData = UserProfileUpdateRequest(nombre = nombre, correo = correo)
                )
                if (response.isSuccessful && response.body() != null) {
                    // Backend solo retorna message; refrescamos el perfil
                    fetchUserProfile()
                } else {
                    _profileUiState.value = ProfileUiState.Error(response.errorBody()?.string() ?: "Error al actualizar perfil")
                }
            } catch (e: Exception) {
                _profileUiState.value = ProfileUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun uploadProfilePhoto(photo: MultipartBody.Part) {
        viewModelScope.launch {
            _profileUiState.value = ProfileUiState.Loading
            try {
                val token = userPreferencesRepository.authToken.firstOrNull()
                if (token.isNullOrBlank()) {
                    _profileUiState.value = ProfileUiState.Error("Token no disponible")
                    return@launch
                }
                val response = authRepository.uploadProfilePhoto("Bearer $token", photo)
                if (response.isSuccessful) {
                    // Tras subir, refrescar perfil para obtener nueva URL
                    fetchUserProfile()
                } else {
                    _profileUiState.value = ProfileUiState.Error(response.errorBody()?.string() ?: "Error al subir foto")
                }
            } catch (e: Exception) {
                _profileUiState.value = ProfileUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}
