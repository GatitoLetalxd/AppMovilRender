package com.example.projectofinal.ui.theme

import androidx.compose.ui.graphics.Color

// Modo Oscuro - Paleta Principal
val DarkPrimary = Color(0xFF90CAF9)        // Azul claro para primario
val DarkOnPrimary = Color(0xFF003366)      // Texto/iconos oscuros sobre el primario claro
val DarkPrimaryContainer = Color(0xFF0D47A1) // Un azul más oscuro si necesitas un contenedor primario
val DarkOnPrimaryContainer = DarkPrimary     // Texto claro sobre el contenedor primario oscuro

val DarkSecondary = Color(0xFFF48FB1)      // Rosa suave para secundario
val DarkOnSecondary = Color(0xFF6A003A)    // Texto/iconos oscuros sobre el secundario
val DarkSecondaryContainer = Color(0xFFC51162) // Un rosa más oscuro si necesitas contenedor secundario
val DarkOnSecondaryContainer = DarkSecondary // Texto claro sobre contenedor secundario oscuro

val DarkBackgroundDefault = Color(0xFF0A1929) // Fondo general (azul oscuro casi negro)
val DarkOnBackground = Color(0xDDEEEEEE)     // Texto blanco con opacidad (0.87 alfa ~ DD en hexadecimal)

val DarkSurfacePaper = Color(0xFF1A2027)  // Fondo para "paper" como Cards (gris oscuro)
val DarkOnSurface = Color(0xDDEEEEEE)      // Texto blanco con opacidad sobre superficies

// Colores adicionales que mencionaste (similares a los de :root y enlaces)
val LinkColor = Color(0xFF646CFF)         // Azul púrpura para enlaces
val LinkHoverColor = Color(0xFF535BF2)   // (Hover es más complejo en Compose, pero puedes tener el color)
val DefaultButtonBackground = Color(0xFF1A1A1A) // Gris muy oscuro para botones "default" (si no son primary/secondary)

val ErrorColor = Color(0xFFCF6679)        // Color de error estándar para temas oscuros
val OnErrorColor = Color.Black

// Colores específicos para degradados de botones o elementos
val GradientBlueStart = Color(0xFF2196F3)
val GradientCyanEnd = Color(0xFF21CBF3) // Renombrado para claridad (era GradientCyanStart)

// Puedes añadir más si tienes diferentes degradados
val GradientPinkStart = Color(0xFFEC407A)
val GradientPurpleEnd = Color(0xFFAB47BC)
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Estas son las que Material Theme Builder genera más comúnmente ahora:
val md_theme_light_primary = Color(0xFF6750A4)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
// ... y muchos más colores para light y dark theme

val md_theme_dark_primary = Color(0xFFD0BCFF)
val md_theme_dark_onPrimary = Color(0xFF381E72)
// ... y muchos más colores