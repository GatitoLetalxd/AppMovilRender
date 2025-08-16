package com.example.projectofinal.ui.auth

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalGraphicsContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.projectofinal.navigation.AppRoutes
import com.example.projectofinal.navigation.MainScreenRoutes
import com.example.projectofinal.R // Importa los recursos de tu proyecto
import com.example.projectofinal.ui.auth.UploadScreen
import com.example.projectofinal.data.datastore.UserPreferencesRepository
import com.example.projectofinal.data.network.RetrofitInstance
import com.example.projectofinal.data.repository.AuthRepository
import com.example.projectofinal.data.repository.ImageRepository
import com.example.projectofinal.viewmodel.AuthViewModel
import com.example.projectofinal.viewmodel.AuthViewModelFactory
import com.example.projectofinal.viewmodel.ImageViewModel
import com.example.projectofinal.viewmodel.ImageViewModelFactory

sealed class MainBottomNavItem(
    val route: String,
    @DrawableRes val iconResId: Int, // Cambiado a Int para el ID del recurso
    val label: String
) {
    object Upload : MainBottomNavItem(
        MainScreenRoutes.UPLOAD,
        R.drawable.nubeicon, // Reemplaza con el nombre de tu archivo
        "Subir"
    )
    object Images : MainBottomNavItem(
        MainScreenRoutes.IMAGES_PROCESSED,
        R.drawable.imageicon, // Reemplaza con el nombre de tu archivo
        "Imágenes"
    )
    object Videos : MainBottomNavItem(
        MainScreenRoutes.VIDEOS_PROCESSED,
        R.drawable.videoicon, // Reemplaza con el nombre de tu archivo
        "Videos"
    )
    object History : MainBottomNavItem(
        MainScreenRoutes.HISTORY,
        R.drawable.historyicon, // Reemplaza con el nombre de tu archivo
        "Historial"
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    appNavController: NavController
    // Quitamos el authViewModel: AuthViewModel = viewModel() de los parámetros por ahora
) {
    val context = LocalContext.current.applicationContext

    // Crear las dependencias y el ViewModel aquí mismo
    val authViewModel: AuthViewModel = viewModel(
        factory = remember { // Usamos remember para que la factory no se recree innecesariamente
            val userPrefsRepository = UserPreferencesRepository(context)
            // Ajusta la creación de AuthRepository según tus necesidades:
            val authApi = RetrofitInstance.api // Ejemplo
            val authRepo = AuthRepository(authApi) // Ejemplo
            AuthViewModelFactory(authRepo, userPrefsRepository)
        }
    )

    // El resto de tu MainScreen sigue igual...
    val mainScreenNavController = rememberNavController()
    val bottomNavItems = listOf(
        MainBottomNavItem.Upload,
        MainBottomNavItem.Images,
        MainBottomNavItem.Videos,
        MainBottomNavItem.History
    )
    val navBackStackEntry by mainScreenNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: MainScreenRoutes.WELCOME

    val displayName by authViewModel.currentDisplayName.collectAsState(initial = null)

    var topBarTitle by remember { mutableStateOf("") }
    LaunchedEffect(currentRoute, displayName) {
        topBarTitle = when (currentRoute) {
            MainScreenRoutes.WELCOME -> "Inicio"
            else -> bottomNavItems.find { it.route == currentRoute }?.label ?: "Render-TGM"
        }
    }
    // ... (resto del código del Scaffold, TopAppBar, NavigationBar, MainScreenNavHost) ...
    // El código del Scaffold y su contenido no necesita cambiar.
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(topBarTitle) },
                actions = {
                    IconButton(onClick = { appNavController.navigate(AppRoutes.PROFILE_SCREEN) }) {
                        Icon(Icons.Filled.AccountCircle, "Perfil de Usuario")
                    }
                    IconButton(onClick = {
                        authViewModel.logoutUser() // Esto seguirá funcionando
                        appNavController.navigate(AppRoutes.HOME_SCREEN) {
                            popUpTo(appNavController.graph.findStartDestination().id) { inclusive = true }
                            launchSingleTop = true
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, "Cerrar Sesión")
                    }
                }
            )
        },
        bottomBar = {
            if (currentRoute != MainScreenRoutes.WELCOME) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    mainScreenNavController.navigate(item.route) {
                                        popUpTo(mainScreenNavController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { 
                                Image(
                                    painter = painterResource(id = item.iconResId), 
                                    contentDescription = item.label,
                                    modifier = Modifier.size(20.dp)
                                ) 
                            },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        MainScreenNavHost(
            navController = mainScreenNavController,
            userName = displayName,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun MainScreenNavHost(
    navController: NavHostController,
    userName: String?, // Recibe el nombre de usuario
    modifier: Modifier = Modifier
    //, authViewModel: AuthViewModel // Si tus pantallas internas lo necesitan
) {
    NavHost(
        navController = navController,
        startDestination = MainScreenRoutes.WELCOME, // << CAMBIO IMPORTANTE
        modifier = modifier.fillMaxSize()
    ) {
        composable(MainScreenRoutes.WELCOME) {
            WelcomeScreen(
                userName = userName,
                onNavigateToUpload = { navController.navigate(MainScreenRoutes.UPLOAD) },
                onNavigateToImages = { navController.navigate(MainScreenRoutes.IMAGES_PROCESSED) },
                onNavigateToVideos = { navController.navigate(MainScreenRoutes.VIDEOS_PROCESSED) },
                onNavigateToHistory = { navController.navigate(MainScreenRoutes.HISTORY) }
            )
        }
        composable(MainScreenRoutes.UPLOAD) { 
            val imageRepository = ImageRepository(RetrofitInstance.api)
            val userPrefsRepository = UserPreferencesRepository(LocalContext.current)
            val imageViewModel: ImageViewModel = viewModel(
                factory = ImageViewModelFactory(imageRepository, userPrefsRepository)
            )
            
            UploadScreen(
                onNavigateBack = { navController.navigateUp() },
                imageViewModel = imageViewModel
            )
        }
        composable(MainScreenRoutes.IMAGES_PROCESSED) { ImagesProcessedScreenPlaceholder() }
        composable(MainScreenRoutes.VIDEOS_PROCESSED) { VideosProcessedScreenPlaceholder() }
        composable(MainScreenRoutes.HISTORY) { HistoryScreenPlaceholder() }
    }
}
// --- Componibles de Marcador de Posición para cada Sección Interna ---
// --- Más adelante crearás archivos separados para estos en sus respectivos directorios ---
@Composable
fun UploadScreenPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Pantalla Subir Imágenes", style = MaterialTheme.typography.headlineMedium)
    }
}
@Composable
fun ImagesProcessedScreenPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Pantalla Imágenes Procesadas", style = MaterialTheme.typography.headlineMedium)
    }
}
@Composable
fun VideosProcessedScreenPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Pantalla Videos Procesados", style = MaterialTheme.typography.headlineMedium)
    }
}
@Composable
fun HistoryScreenPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Pantalla Historial", style = MaterialTheme.typography.headlineMedium)
    }
}
