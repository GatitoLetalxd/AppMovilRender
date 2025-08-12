package com.example.projectofinal.ui.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun UploadScreen() { // Renombrado de UploadScreenPlaceholder
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Pantalla Subir Imágenes", style = MaterialTheme.typography.headlineMedium)
        // TODO: Implementar UI de bienvenida y subida de archivos
    }
}