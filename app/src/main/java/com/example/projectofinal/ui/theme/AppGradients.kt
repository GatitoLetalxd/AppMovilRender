package com.example.projectofinal.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Pinceles de degradado comunes
object AppBrushes {
    val primaryGradientButton: Brush = Brush.horizontalGradient(
        colors = listOf(GradientBlueStart, GradientCyanEnd)
        // Puedes añadir colorStops aquí si los necesitas:
        // colorStops = arrayOf(0.0f to GradientBlueStart, 1.0f to GradientCyanEnd)
    )

    val secondaryGradientButton: Brush = Brush.horizontalGradient(
        colors = listOf(GradientPinkStart, GradientPurpleEnd)
    )

    val heroBackground: Brush = Brush.linearGradient(
        colors = listOf(DarkBackgroundDefault, Color(0xFF132F4C)) // Ejemplo de otro degradado
    )

    // Añade más brushes según necesites
}