package com.example.projectofinal.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projectofinal.data.datastore.UserPreferencesRepository
import com.example.projectofinal.data.model.AuthResponse
import com.example.projectofinal.data.model.GenericResponse
import com.example.projectofinal.data.model.LoginRequest
import com.example.projectofinal.data.model.RegisterRequest
import com.example.projectofinal.data.model.UserDetails
import com.example.projectofinal.data.model.UserProfileData
import com.example.projectofinal.data.model.UserProfileUpdateRequest
import com.example.projectofinal.data.network.ApiService
import com.example.projectofinal.data.network.UserProfile
import com.example.projectofinal.data.repository.AuthRepository
import com.example.projectofinal.ui.theme.ProjectoFinalDetailedTheme
import com.example.projectofinal.ui.uistate.AuthUiState
import com.example.projectofinal.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

// Función de utilidad simple para validar email (puede ir fuera o en un archivo de utilidades)
fun String.isValidEmail(): Boolean = android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    // Usar rememberSaveable para que el estado de los campos sobreviva a cambios de configuración simples
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    val registerState by authViewModel.registerUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope() // CoroutineScope ligado al ciclo de vida del Composable

    LaunchedEffect(registerState) {
        when (val state = registerState) {
            is AuthUiState.Success -> {
                // Usar el nombre del backend si está disponible, sino el username del formulario como fallback
                val welcomeName = state.authData.user?.nombre ?: username
                snackbarHostState.showSnackbar(
                    message = "Registro exitoso. ¡Bienvenido $welcomeName!", // <-- MODIFICADO
                    duration = SnackbarDuration.Short
                )
                onRegisterSuccess()
                authViewModel.resetRegisterState()
            }
            is AuthUiState.Error -> {
                snackbarHostState.showSnackbar(
                    message = state.message,
                    duration = SnackbarDuration.Long
                )
                authViewModel.resetRegisterState()
            }
            else -> { /* No hacer nada para Idle o Loading */ }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Crear Cuenta", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nombre de Usuario") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                isError = username.isBlank() && (email.isNotBlank() || password.isNotBlank()) // Mostrar error si está vacío y otros campos tienen datos (opcional)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                isError = email.isNotBlank() && !email.isValidEmail() // <-- VALIDACIÓN DE EMAIL
            )
            if (email.isNotBlank() && !email.isValidEmail()) {
                Text(
                    text = "Formato de correo inválido",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                isError = password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                isError = password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword
            )
            if (password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword) {
                Text(
                    text = "Las contraseñas no coinciden",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            val formIsValid = username.isNotBlank() &&
                    email.isNotBlank() && email.isValidEmail() &&
                    password.isNotBlank() && confirmPassword.isNotBlank() &&
                    password == confirmPassword

            when (registerState) {
                is AuthUiState.Loading -> {
                    CircularProgressIndicator()
                }
                else -> {
                    Button(
                        onClick = {
                            // Validaciones adicionales antes de llamar al ViewModel (ya cubiertas por 'formIsValid' y checks individuales)
                            if (username.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                                scope.launch { // <-- USANDO SCOPE RECORDADO
                                    snackbarHostState.showSnackbar("Por favor, completa todos los campos.")
                                }
                                return@Button
                            }
                            if (!email.isValidEmail()) {
                                scope.launch { // <-- USANDO SCOPE RECORDADO
                                    snackbarHostState.showSnackbar("Por favor, introduce un correo electrónico válido.")
                                }
                                return@Button
                            }
                            if (password != confirmPassword) {
                                scope.launch { // <-- USANDO SCOPE RECORDADO
                                    snackbarHostState.showSnackbar("Las contraseñas no coinciden.")
                                }
                                return@Button
                            }
                            authViewModel.registerUser(username, email, password)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = formIsValid && registerState !is AuthUiState.Loading // Usar la variable de validación
                    ) {
                        Text("Registrarse")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text("¿Ya tienes cuenta? Inicia Sesión")
            }
        }
    }
}
/*
@Preview(showBackground = true, device = "spec:width=1080px,height=2220px,dpi=440")
@Composable
fun RegisterScreenPreview() {
    val context = LocalContext.current

    // Fake ApiService para el Preview
    class FakeApiServicePreview : ApiService {
        override suspend fun loginUser(loginRequest: LoginRequest): retrofit2.Response<AuthResponse> {
            TODO("Not relevantly implemented for RegisterScreen preview")
        }

        override suspend fun registerUser(registerRequest: RegisterRequest): retrofit2.Response<AuthResponse> {
            // Asumiendo que tu RegisterRequest tiene un campo llamado 'correo_electronico'
            // Si se llama 'correo', usa registerRequest.correo
            println("FakeApiServicePreview: Registering user ${registerRequest.correo}")

            // Simula una respuesta exitosa para el preview
            val fakeUserDetails = UserDetails( // <-- USERDETAILS FALSO
                id = 12345,
                nombre = "Preview User",
                correo = registerRequest.correo, // Usar el campo correcto de RegisterRequest
                rol = "usuario"
                // No fotoPerfilUrl aquí, ¡correcto!
            )
            return retrofit2.Response.success(
                AuthResponse( // <-- AUTHRESPONSE CON USERDETAILS
                    token = "fake_token_for_preview_register",
                    user = fakeUserDetails,
                    message = "Registro de Preview Exitoso"
                )
            )
        }

        // Estas funciones deben coincidir con la firma de tu ApiService real
        override suspend fun getUserProfile(token: String): retrofit2.Response<UserProfileData> { // <--- CAMBIADO a UserProfileData
            println("FakeApiServicePreview: Getting user profile. Token: $token")
            val fakeProfile = UserProfileData(
                id = 123,
                nombre = "Fake Profile User",
                correo = "fake.profile@example.com",
                rol = "usuario",
                fotoPerfilUrl = "/uploads/fake.jpg",
                fechaRegistro = "2023-01-01T00:00:00.000Z"
            )
            return retrofit2.Response.success(fakeProfile)
            // O si no es necesario para este preview específico:
            // TODO("Not relevantly implemented for RegisterScreen preview. Token: $token")
        }

        override suspend fun updateUserProfile(
            token: String,
            profileData: UserProfileUpdateRequest
        ): retrofit2.Response<UserProfileData> { // <--- CAMBIADO a UserProfileData (si la API devuelve el perfil actualizado)
            println("FakeApiServicePreview: Updating user profile. Token: $token, Data: $profileData")
            val updatedProfile = UserProfileData(
                id = profileData.id?: 123,
                nombre = profileData.nombre ?: "Updated Name",
                correo = profileData.correo ?: "updated.profile@example.com",
                rol = "usuario",
                fotoPerfilUrl = "/uploads/updated.jpg",
                fechaRegistro = "2023-01-01T00:00:00.000Z"
            )
            return retrofit2.Response.success(updatedProfile)
            // O si no es necesario para este preview específico:
            // TODO("Not relevantly implemented for RegisterScreen preview. Token: $token, Data: $profileData")
        }

        override suspend fun uploadProfilePhoto(
            token: String,
            photo: MultipartBody.Part
        ): retrofit2.Response<GenericResponse> {
            TODO("Not relevantly implemented for RegisterScreen preview. Token: $token, Photo: ${photo.headers}")
        }
    }

    val dummyAuthRepository = AuthRepository(FakeApiServicePreview())
    val dummyUserPrefsRepo = UserPreferencesRepository(context)


    class PreviewAuthViewModelFactory(
        private val repo: AuthRepository,
        private val prefs: UserPreferencesRepository
    ) : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(repo, prefs) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class for preview")
        }
    }

    val dummyAuthViewModel: AuthViewModel = viewModel(
        factory = PreviewAuthViewModelFactory(dummyAuthRepository, dummyUserPrefsRepo)
    )

    ProjectoFinalDetailedTheme {
        RegisterScreen(
            authViewModel = dummyAuthViewModel,
            onRegisterSuccess = { println("Preview: Register Success!") },
            onNavigateToLogin = { println("Preview: Navigate to Login!") }
        )
    }
}
*/