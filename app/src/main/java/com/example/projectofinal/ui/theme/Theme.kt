package com.example.projectofinal.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AppDarkDetailedColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,

    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,

    tertiary = DarkSecondary, // Puedes definir un terciario si es diferente
    onTertiary = DarkOnSecondary,
    tertiaryContainer = DarkSecondaryContainer,
    onTertiaryContainer = DarkOnSecondaryContainer,

    error = ErrorColor,
    onError = OnErrorColor,
    errorContainer = ErrorColor, // Ajusta si necesitas un contenedor de error diferente
    onErrorContainer = OnErrorColor,

    background = DarkBackgroundDefault,
    onBackground = DarkOnBackground,

    surface = DarkSurfacePaper, // Usamos 'paper' como el color principal para 'surface'
    onSurface = DarkOnSurface,

    surfaceVariant = DarkSurfacePaper.copy(alpha = 0.7f), // Un 'paper' ligeramente más transparente o diferente
    onSurfaceVariant = DarkOnSurface,

    outline = DarkSecondary.copy(alpha = 0.5f) // Borde con el secundario y algo de alfa
)

// Podrías tener un LightColorScheme también si lo necesitas
// private val AppLightDetailedColorScheme = lightColorScheme(...)

@Composable
fun ProjectoFinalDetailedTheme(
    darkTheme: Boolean = true, // Forzamos el tema oscuro por ahora, como en tu ejemplo
    dynamicColor: Boolean = false, // Dinamic color de Android 12+ (opcional)
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        AppDarkDetailedColorScheme
    } else {
        // AppLightDetailedColorScheme
        AppDarkDetailedColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb() // Fondo de la barra de estado
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Asume que tienes Typography.kt
        content = content
    )
}