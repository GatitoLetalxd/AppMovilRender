package com.example.projectofinal.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projectofinal.ui.auth.HomeScreen
import com.example.projectofinal.ui.auth.LoginScreen
import com.example.projectofinal.ui.auth.MainScreen
import com.example.projectofinal.ui.auth.ProfileScreen
import com.example.projectofinal.ui.auth.RegisterScreen
import com.example.projectofinal.viewmodel.AuthViewModel

@Composable
fun AppNavigationGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel
) {
    val authToken by authViewModel.authTokenStream.collectAsState(initial = null)
    var startDestination by remember { mutableStateOf<String?>(null) }

    // Este LaunchedEffect determina la ruta inicial basándose en el estado de autenticación
    LaunchedEffect(authToken) {
        startDestination = if (authToken != null) AppRoutes.MAIN_SCREEN else AppRoutes.HOME_SCREEN
    }

    // El NavHost solo se compone cuando el destino inicial ya ha sido determinado
    startDestination?.let { startDest ->
        NavHost(
            navController = navController,
            startDestination = startDest
        ) {
            composable(AppRoutes.HOME_SCREEN) {
                HomeScreen(
                    onLoginClicked = { navController.navigate(AppRoutes.LOGIN_SCREEN) },
                    onRegisterClicked = { navController.navigate(AppRoutes.REGISTER_SCREEN) }
                )
            }
            composable(AppRoutes.LOGIN_SCREEN) {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate(AppRoutes.MAIN_SCREEN) {
                            popUpTo(AppRoutes.HOME_SCREEN) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(AppRoutes.REGISTER_SCREEN)
                    }
                )
            }
            composable(AppRoutes.REGISTER_SCREEN) {
                RegisterScreen(
                    authViewModel = authViewModel,
                    onRegisterSuccess = {
                        navController.navigate(AppRoutes.LOGIN_SCREEN) {
                            popUpTo(AppRoutes.HOME_SCREEN)
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(AppRoutes.LOGIN_SCREEN) {
                            popUpTo(AppRoutes.REGISTER_SCREEN) { inclusive = true }
                        }
                    }
                )
            }
            composable(AppRoutes.MAIN_SCREEN) {
                MainScreen(appNavController = navController)
            }
            composable(AppRoutes.PROFILE_SCREEN) {
                // Pasando los parámetros correctos
                ProfileScreen(navController = navController, authViewModel = authViewModel)
            }
        }
    } ?: run {
        // Muestra un indicador de carga mientras se determina la ruta inicial
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}