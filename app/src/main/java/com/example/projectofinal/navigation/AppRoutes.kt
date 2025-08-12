package com.example.projectofinal.navigation

object AppRoutes {
    const val HOME_SCREEN = "home"
    const val LOGIN_SCREEN = "login"
    const val REGISTER_SCREEN = "register"
    const val MAIN_SCREEN = "main" // O "dashboard_screen", "home_screen", etc.
    const val PROFILE_SCREEN = "profile" // Nueva ruta para el perfil
    // Podrías tener rutas más complejas con argumentos más adelante
}
object MainScreenRoutes {
    const val WELCOME = "main_welcome" // <<< Nueva ruta de bienvenida
    const val UPLOAD = "main_upload"
    const val IMAGES_PROCESSED = "main_images_processed"
    const val VIDEOS_PROCESSED = "main_videos_processed"
    const val HISTORY = "main_history"
    // Podrías añadir la ruta del perfil aquí también si se maneja desde este NavController
    // o mantenerla como una ruta de nivel superior si ProfileScreen es más independiente.
    // Por ahora, la manejaremos como una navegación separada desde la TopAppBar.
}