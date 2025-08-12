package com.example.projectofinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel // Importante para el viewModel delegado
import com.example.projectofinal.navigation.AppNavigationGraph
import com.example.projectofinal.ui.theme.ProjectoFinalDetailedTheme
import com.example.projectofinal.viewmodel.AuthViewModel
import com.example.projectofinal.viewmodel.ViewModelFactoryHelper
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Opcional, para dibujar detrás de las barras del sistema
        setContent {
            ProjectoFinalDetailedTheme { // <--- Aquí se aplica tu tema a toda la app
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background // El fondo general vendrá del tema
                ) {
                    // Obtén el AuthViewModel aquí usando la factory
                    val authViewModelFactory = ViewModelFactoryHelper.provideAuthViewModelFactory(LocalContext.current)
                    val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)

                    AppNavigationGraph(authViewModel = authViewModel)
                }
            }
        }
    }
}