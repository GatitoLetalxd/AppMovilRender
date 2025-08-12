import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator // Asegúrate de importar esto
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projectofinal.navigation.AppRoutes
import com.example.projectofinal.ui.auth.HomeScreen // Asegúrate que la ruta a HomeScreen es correcta
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
    var startDestinationForNavHost by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = authToken) {
        if (authToken != null) {
            startDestinationForNavHost = AppRoutes.MAIN_SCREEN
            if (navController.currentBackStackEntry?.destination?.route != AppRoutes.MAIN_SCREEN &&
                navController.graph.startDestinationRoute == AppRoutes.HOME_SCREEN) {
                navController.navigate(AppRoutes.MAIN_SCREEN) {
                    popUpTo(AppRoutes.HOME_SCREEN) { inclusive = true }
                    launchSingleTop = true
                }
            }
        } else {
            startDestinationForNavHost = AppRoutes.HOME_SCREEN
            if (navController.currentBackStackEntry?.destination?.route == AppRoutes.MAIN_SCREEN) {
                navController.navigate(AppRoutes.HOME_SCREEN) {
                    popUpTo(AppRoutes.MAIN_SCREEN) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    if (startDestinationForNavHost != null) {
        NavHost(
            navController = navController,
            startDestination = startDestinationForNavHost!!
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
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
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
                        // Navegar a LoginScreen después del registro exitoso
                        navController.navigate(AppRoutes.LOGIN_SCREEN) {
                            popUpTo(AppRoutes.HOME_SCREEN) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(AppRoutes.LOGIN_SCREEN) {
                            popUpTo(AppRoutes.REGISTER_SCREEN) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(AppRoutes.MAIN_SCREEN) {
                // Reemplaza MainScreenPlaceholder con el MainScreen real
                MainScreen(appNavController = navController) // <<< CAMBIO AQUÍ
            }
            composable(AppRoutes.PROFILE_SCREEN) { // <<< NUEVA RUTA
                ProfileScreen(navController = navController)
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}