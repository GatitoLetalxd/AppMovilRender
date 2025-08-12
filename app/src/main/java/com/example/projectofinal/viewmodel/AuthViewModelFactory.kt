package com.example.projectofinal.viewmodel // Ajusta el paquete

import android.content.Context // Necesario para instanciar UserPreferencesRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projectofinal.data.datastore.UserPreferencesRepository // Importa
import com.example.projectofinal.data.repository.AuthRepository
import com.example.projectofinal.data.network.RetrofitInstance

class AuthViewModelFactory(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authRepository, userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

// Helper para crear la factory más fácilmente desde la UI (Activity/Composable)
// Coloca esto en el mismo archivo o en un archivo de utilidades
object ViewModelFactoryHelper {
    fun provideAuthViewModelFactory(context: Context): AuthViewModelFactory {
        val applicationContext = context.applicationContext // Usa el contexto de aplicación
        return AuthViewModelFactory(
            authRepository = AuthRepository(RetrofitInstance.api),
            userPreferencesRepository = UserPreferencesRepository(applicationContext)
        )
    }
}