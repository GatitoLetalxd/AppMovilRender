package com.example.projectofinal.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projectofinal.ui.theme.ProjectoFinalDetailedTheme
import com.example.projectofinal.ui.uistate.AuthUiState
import com.example.projectofinal.utils.ValidationUtils
import com.example.projectofinal.viewmodel.AuthViewModel
import com.example.projectofinal.viewmodel.ViewModelFactoryHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by authViewModel.loginUiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Validación en tiempo real usando utilidades centralizadas
    val emailError = remember(email) { ValidationUtils.getEmailErrorMessage(email) }
    val passwordError = remember(password) { ValidationUtils.getPasswordErrorMessage(password) }

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is AuthUiState.Success -> {
                snackbarHostState.showSnackbar(
                    message = "Login exitoso. Bienvenido ${state.authData.user?.nombre ?: "Usuario"}!",
                    duration = SnackbarDuration.Short
                )
                onLoginSuccess()
                authViewModel.resetLoginState()
            }
            is AuthUiState.Error -> {
                snackbarHostState.showSnackbar(
                    message = state.message,
                    duration = SnackbarDuration.Long
                )
                authViewModel.resetLoginState()
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
            Text("Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                isError = emailError != null,
                supportingText = {
                    emailError?.let { error ->
                        Text(error)
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                isError = passwordError != null,
                supportingText = {
                    passwordError?.let { error ->
                        Text(error)
                    }
                }
            )
            Spacer(modifier = Modifier.height(24.dp))

            when (loginState) {
                is AuthUiState.Loading -> {
                    CircularProgressIndicator()
                }
                else -> {
                    Button(
                        onClick = {
                            if (email.isNotBlank() && password.isNotBlank()) {
                                authViewModel.loginUser(email, password)
                            } else {
                                CoroutineScope(Dispatchers.Main).launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Por favor, completa todos los campos.",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = email.isNotBlank() && password.isNotBlank() && 
                                 emailError == null && passwordError == null && 
                                 loginState !is AuthUiState.Loading
                    ) {
                        Text("Ingresar")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text("¿No tienes cuenta? Regístrate aquí")
            }
        }
    }
}

// Preview simplificado
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    ProjectoFinalDetailedTheme {
        // Preview básico sin ViewModel real
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Text("LoginScreen Preview")
        }
    }
}