package com.example.projectofinal.ui.uistate

import com.example.projectofinal.data.model.Friend
import com.example.projectofinal.data.model.FriendRequest
import com.example.projectofinal.data.model.UserProfile
import com.example.projectofinal.data.model.UserSearchResult

// Estado para la información del perfil de usuario
sealed class ProfileUiState {
    object Idle : ProfileUiState()
    object Loading : ProfileUiState()
    data class Success(val userProfile: UserProfile) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

// Estado para la lista de amigos
sealed class FriendsUiState {
    object Idle : FriendsUiState()
    object Loading : FriendsUiState()
    data class Success(val friends: List<Friend>) : FriendsUiState()
    data class Error(val message: String) : FriendsUiState()
}

// Estado para las solicitudes de amistad pendientes
sealed class FriendRequestsUiState {
    object Idle : FriendRequestsUiState()
    object Loading : FriendRequestsUiState()
    data class Success(val requests: List<FriendRequest>) : FriendRequestsUiState()
    data class Error(val message: String) : FriendRequestsUiState()
}

// Estado para la búsqueda de usuarios
sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val results: List<UserSearchResult>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

// Estado para acciones (enviar, aceptar, rechazar solicitud)
sealed class ActionUiState {
    object Idle : ActionUiState()
    object Loading : ActionUiState()
    data class Success(val message: String) : ActionUiState()
    data class Error(val message: String) : ActionUiState()
}

// Solicitudes de admin
sealed class AdminRequestsUiState {
    object Idle : AdminRequestsUiState()
    object Loading : AdminRequestsUiState()
    data class Success(val requests: List<com.example.projectofinal.data.model.AdminRequest>) : AdminRequestsUiState()
    data class Error(val message: String) : AdminRequestsUiState()
}

// Listado de usuarios
sealed class AdminUsersUiState {
    object Idle : AdminUsersUiState()
    object Loading : AdminUsersUiState()
    data class Success(val users: List<com.example.projectofinal.data.model.UserSummary>) : AdminUsersUiState()
    data class Error(val message: String) : AdminUsersUiState()
}