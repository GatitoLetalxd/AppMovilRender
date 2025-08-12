package com.example.projectofinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.projectofinal.data.datastore.UserPreferencesRepository
import com.example.projectofinal.data.model.LoginRequest
import com.example.projectofinal.data.model.RegisterRequest
import com.example.projectofinal.data.repository.AuthRepository
import com.example.projectofinal.ui.uistate.AuthUiState
import com.example.projectofinal.utils.AppConstants
import com.example.projectofinal.utils.Logger
import com.example.projectofinal.utils.ValidationUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    companion object {
        private const val TAG = "AuthViewModel"
    }

    private val _loginUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val loginUiState: StateFlow<AuthUiState> = _loginUiState.asStateFlow()

    private val _registerUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val registerUiState: StateFlow<AuthUiState> = _registerUiState.asStateFlow()

    val authTokenStream: Flow<String?> = userPreferencesRepository.authToken

    val currentDisplayName: StateFlow<String?> = userPreferencesRepository.displayName
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val currentUserId: StateFlow<String?> = userPreferencesRepository.userId
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val currentUserEmail: StateFlow<String?> = userPreferencesRepository.userEmail
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Función de validación de entrada usando utilidades centralizadas
    fun validateInputs(email: String, password: String): ValidationResult {
        val emailError = ValidationUtils.getEmailErrorMessage(email)
        val passwordError = ValidationUtils.getPasswordErrorMessage(password)
        
        return when {
            emailError != null -> ValidationResult.Error(emailError)
            passwordError != null -> ValidationResult.Error(passwordError)
            else -> ValidationResult.Success
        }
    }

    fun loginUser(emailInput: String, passwordInput: String) {
        Logger.logUserAction(TAG, "Login initiated", emailInput)
        
        // Validar entrada antes de proceder
        val validation = validateInputs(emailInput, passwordInput)
        if (validation is ValidationResult.Error) {
            _loginUiState.value = AuthUiState.Error(validation.message)
            return
        }

        viewModelScope.launch {
            _loginUiState.value = AuthUiState.Loading
            try {
                val loginRequest = LoginRequest(emailInput, passwordInput)
                Logger.logNetworkCall(TAG, "auth/login", "POST")
                
                val response = authRepository.loginUser(loginRequest)

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    Logger.logNetworkSuccess(TAG, "auth/login", response.code())

                    val token = authResponse.token
                    val userId = authResponse.user?.id?.toString()
                    val displayNameFromBackend = authResponse.user?.nombre
                    val emailFromBackend = authResponse.user?.correo

                    if (token != null && userId != null && displayNameFromBackend != null) {
                        Logger.logDataOperation(TAG, "Save auth details", "user credentials", true)
                        userPreferencesRepository.saveAuthDetails(
                            token = token,
                            userId = userId,
                            displayName = displayNameFromBackend,
                            email = emailFromBackend
                        )
                        _loginUiState.value = AuthUiState.Success(authResponse)
                        Logger.logUserAction(TAG, "Login completed", userId)
                    } else {
                        val errorMsg = AppConstants.ErrorMessages.INCOMPLETE_RESPONSE
                        Logger.w(TAG, errorMsg)
                        _loginUiState.value = AuthUiState.Error(errorMsg)
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: AppConstants.ErrorMessages.UNKNOWN_ERROR
                    Logger.logNetworkError(TAG, "auth/login", errorMsg)
                    _loginUiState.value = AuthUiState.Error(errorMsg)
                }
            } catch (e: IOException) {
                val errorMsg = AppConstants.ErrorMessages.NETWORK_ERROR
                Logger.logNetworkError(TAG, "auth/login", errorMsg, e)
                _loginUiState.value = AuthUiState.Error(errorMsg)
            } catch (e: Exception) {
                val errorMsg = e.message ?: AppConstants.ErrorMessages.UNKNOWN_ERROR
                Logger.e(TAG, "Unexpected error in login: $errorMsg", e)
                _loginUiState.value = AuthUiState.Error(errorMsg)
            }
        }
    }

    fun registerUser(usernameInput: String, emailInput: String, passwordInput: String) {
        Logger.logUserAction(TAG, "Register initiated", emailInput)
        
        // Validar entrada antes de proceder
        val validation = validateInputs(emailInput, passwordInput)
        if (validation is ValidationResult.Error) {
            _registerUiState.value = AuthUiState.Error(validation.message)
            return
        }
        
        val usernameError = ValidationUtils.getUsernameErrorMessage(usernameInput)
        if (usernameError != null) {
            _registerUiState.value = AuthUiState.Error(usernameError)
            return
        }

        viewModelScope.launch {
            _registerUiState.value = AuthUiState.Loading
            try {
                val registerRequest = RegisterRequest(usernameInput, emailInput, passwordInput)
                Logger.logNetworkCall(TAG, "auth/register", "POST")
                
                val response = authRepository.registerUser(registerRequest)

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    Logger.logNetworkSuccess(TAG, "auth/register", response.code())

                    val token = authResponse.token
                    val userId = authResponse.user?.id?.toString()
                    val displayNameFromBackend = authResponse.user?.nombre
                    val emailFromBackend = authResponse.user?.correo

                    if (token != null && userId != null && displayNameFromBackend != null) {
                        Logger.logDataOperation(TAG, "Save auth details", "user credentials", true)
                        userPreferencesRepository.saveAuthDetails(
                            token = token,
                            userId = userId,
                            displayName = displayNameFromBackend,
                            email = emailFromBackend
                        )
                        _registerUiState.value = AuthUiState.Success(authResponse)
                        Logger.logUserAction(TAG, "Register completed", userId)
                    } else {
                        val errorMsg = AppConstants.ErrorMessages.INCOMPLETE_RESPONSE
                        Logger.w(TAG, errorMsg)
                        _registerUiState.value = AuthUiState.Error(errorMsg)
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: AppConstants.ErrorMessages.UNKNOWN_ERROR
                    Logger.logNetworkError(TAG, "auth/register", errorMsg)
                    _registerUiState.value = AuthUiState.Error(errorMsg)
                }
            } catch (e: IOException) {
                val errorMsg = AppConstants.ErrorMessages.NETWORK_ERROR
                Logger.logNetworkError(TAG, "auth/register", errorMsg, e)
                _registerUiState.value = AuthUiState.Error(errorMsg)
            } catch (e: Exception) {
                val errorMsg = e.message ?: AppConstants.ErrorMessages.UNKNOWN_ERROR
                Logger.e(TAG, "Unexpected error in register: $errorMsg", e)
                _registerUiState.value = AuthUiState.Error(errorMsg)
            }
        }
    }

    fun logoutUser() {
        Logger.logUserAction(TAG, "Logout initiated")
        viewModelScope.launch {
            userPreferencesRepository.clearAuthCredentials()
            _loginUiState.value = AuthUiState.Idle
            _registerUiState.value = AuthUiState.Idle
            Logger.logUserAction(TAG, "Logout completed")
        }
    }

    fun resetLoginState() {
        _loginUiState.value = AuthUiState.Idle
    }

    fun resetRegisterState() {
        _registerUiState.value = AuthUiState.Idle
    }
}

// Clase para manejar resultados de validación
sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}
