package com.example.projectofinal.ui.uistate

import com.example.projectofinal.data.model.AuthResponse

sealed interface AuthUiState {
    object Idle : AuthUiState // Estado inicial, no se ha hecho nada
    object Loading : AuthUiState // La operación está en progreso
    data class Success(val authData: AuthResponse) : AuthUiState // La operación fue exitosa
    data class Error(val message: String) : AuthUiState // Ocurrió un error
}