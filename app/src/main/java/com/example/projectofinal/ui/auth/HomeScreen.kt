package com.example.projectofinal.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme // Para acceder a colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
// import androidx.compose.ui.graphics.Shape // Calificador redundante eliminado
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.projectofinal.R // Importa tu clase R
import com.example.projectofinal.ui.theme.AppBrushes
import com.example.projectofinal.ui.theme.GradientButton
import com.example.projectofinal.ui.theme.ProjectoFinalDetailedTheme
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
// HorizontalPagerIndicator no está disponible en la nueva API, usaremos uno personalizado
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onLoginClicked: () -> Unit,
    onRegisterClicked: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
        // Ya no necesitas padding-bottom aquí si la última sección maneja su propio padding.
        // O puedes mantenerlo si lo prefieres.
        // .padding(bottom = 16.dp)
    ) {
        // --- NUEVA SECCIÓN DE HÉROE ---
        HeroSectionWithCarouselBackground(
            onLoginClicked = onLoginClicked,
            onRegisterClicked = onRegisterClicked,
            imageList = listOf(
                R.drawable.imagen1,
                R.drawable.imagen2,
                R.drawable.imagen3
            )
        )
        // Ya no necesitas HeaderSection(), Spacer, ActionButtons(), Spacer, ni el primer ImageCarousel() aquí.

        Spacer(modifier = Modifier.height(32.dp)) // Espacio después de la sección del héroe

        // --- SEGUNDO CARRUSEL Y RESTO DEL CONTENIDO (SIN CAMBIOS) ---
        Text(
            text = "NUESTROS TRABAJOS",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        ImageCarousel( // Este es tu segundo carrusel, se mantiene igual
            title = null,
            imageList = listOf(
                R.drawable.imagen4,
                R.drawable.imagen5,
                R.drawable.imagen6
            ),
            autoScroll = false
        )

        Spacer(modifier = Modifier.height(32.dp))
        FeaturesSection()
        Spacer(modifier = Modifier.height(32.dp))
        ContactSection()
        // Considera añadir un Spacer al final para que el último elemento no quede pegado
        // si el contenido es corto y no se puede hacer scroll.
        Spacer(modifier = Modifier.height(16.dp))
    }
}
@Composable
fun HeroSectionWithCarouselBackground(
    onLoginClicked: () -> Unit,
    onRegisterClicked: () -> Unit,
    imageList: List<Int> // Las imágenes para el fondo
) {
    val pagerState = rememberPagerState(pageCount = { imageList.size })
    val coroutineScope = rememberCoroutineScope()
    val autoScroll = true
    val scrollDurationMillis = 3000L

    // Lógica de Autoscroll
    LaunchedEffect(pagerState.currentPage, autoScroll) {
        if (autoScroll && imageList.size > 1) {
            while (true) {
                delay(scrollDurationMillis)
                coroutineScope.launch {
                    val nextPage = (pagerState.currentPage + 1) % imageList.size
                    pagerState.animateScrollToPage(page = nextPage)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            // Define una altura fija para esta sección de \"héroe\".
            // Esta altura debe ser suficiente para mostrar bien las imágenes y el contenido.
            .height(400.dp) // --- AJUSTA ESTA ALTURA ---
    ) {
        // CAPA 1: Carrusel de Imágenes de Fondo
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize() // El Pager ocupa todo el Box
        ) { page: Int ->
            Image(
                painter = painterResource(id = imageList[page]),
                contentDescription = "Fondo de carrusel ${page + 1}",
                contentScale = ContentScale.Crop, // Crop para llenar el espacio
                modifier = Modifier
                    .fillMaxSize()
                // No necesitas .clip() aquí si quieres que la imagen llene todo el Box
            )
        }

        // CAPA 2: (Opcional) Velo para mejorar contraste
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f)) // --- AJUSTA EL ALFA (0.0 a 1.0) ---
        )

        // CAPA 3: Contenido (Header y Botones)
        Column(
            modifier = Modifier
                .fillMaxSize() // Ocupa todo el Box para permitir alineación interna
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Centra el contenido verticalmente
        ) {
            // Contenido de tu HeaderSection (adaptado)
            Text(
                text = "RENDER-TGM",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White, // --- CAMBIO DE COLOR PARA LEGIBILIDAD ---
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Transformamos tus imágenes con inteligencia artificial",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.85f), // --- CAMBIO DE COLOR ---
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp)) // Espacio antes de los botones

            // Tus ActionButtons (adaptados si es necesario)
            ActionButtons(
                onLoginClicked = onLoginClicked,
                onRegisterClicked = onRegisterClicked,
                // Puedes pasar modificadores o colores si necesitas ajustar más los botones
                // Para este ejemplo, asumimos que los colores de texto dentro de ActionButtons
                // ya fueron ajustados o que los botones tienen un fondo que contrasta.
            )
        }

        // CAPA 4: (Opcional) Indicadores del Pager
        if (imageList.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(imageList.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = if (pagerState.currentPage == index) Color.White else Color.White.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

/*@Composable
fun HeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "RENDER-TGM",
            style = MaterialTheme.typography.displayMedium, // Tamaño grande para el título principal
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Transformamos tus imágenes con inteligencia artificial",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}*/
@Composable
fun ActionButtons(onLoginClicked: () -> Unit, onRegisterClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GradientButton(
            onClick = onLoginClicked,
            text = "INICIAR SESIÓN",
            gradientBrush = AppBrushes.primaryGradientButton, // Usa tu brush definido
            modifier = Modifier.weight(1f),
            textColor = Color.White // O un color específico si el onPrimary no encaja con el degradado
        )
        GradientButton(
            onClick = onRegisterClicked,
            text = "REGISTRARSE",
            gradientBrush = AppBrushes.secondaryGradientButton, // O un brush diferente
            modifier = Modifier.weight(1f),
            textColor = MaterialTheme.colorScheme.onSecondary // O el color apropiado
        )
    }
}

@Composable
fun ImageCarousel(
    title: String? = null,
    imageList: List<Int>, // Lista de IDs de Drawable
    autoScroll: Boolean = true,
    scrollDurationMillis: Long = 3000L // 3 segundos por imagen
) {
    val pagerState = rememberPagerState(pageCount = { imageList.size })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage, autoScroll) {
        if (autoScroll && imageList.size > 1) {
            while (true) {
                delay(scrollDurationMillis)
                coroutineScope.launch {
                    val nextPage = (pagerState.currentPage + 1) % imageList.size
                    pagerState.animateScrollToPage(page = nextPage)
                }
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        title?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // Ajusta la altura según tus imágenes
        ) { page: Int ->
            Image(
                painter = painterResource(id = imageList[page]),
                contentDescription = "Imagen de carrusel ${page + 1}",
                contentScale = ContentScale.Crop, // O ContentScale.Fit, según prefieras
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp) // Espacio entre imágenes si se ven parcialmente las adyacentes
                    .clip(RoundedCornerShape(12.dp))
            )
        }

        if (imageList.size > 1) {
            Row(
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(imageList.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureItem(
    iconPainter: Painter,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    boxWidth: Dp = 300.dp,  // Ancho deseado para el rectángulo
    boxHeight: Dp = 120.dp, // Alto deseado para el rectángulo
    iconSize: Dp = 40.dp, // Ajusta el tamaño del icono si es necesario
    borderBrush: Brush? = null, // Nuevo: para borde con degradado
    backgroundBrush: Brush = Brush.linearGradient(
        colors = listOf(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
    ),
    borderColor: Color = MaterialTheme.colorScheme.outline,
    borderWidth: Dp = 1.dp,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(12.dp), // Calificador redundante eliminado
) {
    val borderModifier = if (borderBrush != null) {
        Modifier.border(borderWidth, borderBrush, shape)
    } else {
        Modifier.border(borderWidth, borderColor, shape)
    }
    Column(
        modifier = modifier
            .width(boxWidth)   // Establece el ancho
            .height(boxHeight) // Establece el alto
            .clip(shape)
            .background(brush = backgroundBrush) // <--- Cambio clave: especificar 'brush ='
            //.border(borderWidth, borderColor, shape)
            .then(borderModifier) // Aplicar el borde aquí
            .padding(12.dp), // Puedes ajustar el padding si cambias las dimensiones
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // O Arrangement.Top si prefieres
    ) {
        Image(
            painter = iconPainter,
            contentDescription = title,
            modifier = Modifier
                .size(iconSize)
                .padding(bottom = 8.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall, // Podrías necesitar ajustar el estilo del texto
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            maxLines = 2, // Considera limitar las líneas si el espacio es reducido
            overflow = TextOverflow.Ellipsis, // Qué hacer si el texto es muy largo
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.87f)
        )
    }
}
@Composable
fun FeaturesSection() {

    val itemBackgroundGradient = Brush.linearGradient( // O el tipo de gradiente que quieras
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
        ),
        start = Offset(0f, 0f),
        end = Offset(0f, Float.POSITIVE_INFINITY)
    )
    val glowBrush = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre cada FeatureItem
    ) {

        // val subtleBackgroundBrush = ... // Variable no utilizada eliminada

        // Si tienes un título para la sección...
        Text(
            text = "Características Destacadas",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
        FeatureItem(
            iconPainter = painterResource(id = R.drawable.hdicon),
            borderBrush = glowBrush,
            backgroundBrush = itemBackgroundGradient,
            title = "Alta Calidad",
            description = "Resultados profesionales con la mejor calidad de imagen"
        )
        FeatureItem(
            iconPainter = painterResource(id = R.drawable.velocidadicon),
            borderBrush = glowBrush,
            backgroundBrush = itemBackgroundGradient,
            title = "Procesamiento Rápido",
            description = "Obtén tus resultados en cuestión de segundos"
        )
        FeatureItem(
            iconPainter = painterResource(id = R.drawable.nubeicon),
            borderBrush = glowBrush,
            backgroundBrush = itemBackgroundGradient,
            title = "Fácil de Usar",
            description = "Interfaz intuitiva para subir y procesar tus imágenes"
        )
    }
}
@Composable
fun ContactDetailItem(
    iconPainter: Painter,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    boxHeight: Dp = 80.dp,
    iconSize: Dp = 24.dp,
    borderBrush: Brush? = null,
    backgroundBrush: Brush = Brush.linearGradient(
        colors = listOf(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f))
    ),
    borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
    borderWidth: Dp = 1.dp,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(8.dp),
) {
    // Determina el modificador de borde a usar
    val actualBorderModifier = if (borderBrush != null) {
        Modifier.border(borderWidth, borderBrush, shape)
    } else {
        Modifier.border(borderWidth, borderColor, shape)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(boxHeight)
            .clip(shape) // Es bueno hacer clip antes del fondo y el borde
            .background(brush = backgroundBrush)
            .then(actualBorderModifier) // Aplica el borde elegido DESPUÉS del fondo
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = iconPainter,
            contentDescription = label,
            modifier = Modifier
                .size(iconSize)
                .padding(end = 16.dp),
            contentScale = ContentScale.Fit,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.87f)
            )
        }
    }
}

@Composable
fun ContactSection() {val itemBackgroundGradient = Brush.linearGradient( // O el tipo de gradiente que quieras
    colors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
    ),
    start = Offset(0f, 0f),
    end = Offset(0f, Float.POSITIVE_INFINITY)
)
    // También define el glowBrush si lo usas para el borde
    val glowBrush = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp), // Padding alrededor de la sección
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre cada ContactDetailItem
    ) {

        Text(
            text = "Contáctanos",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp),
            color = MaterialTheme.colorScheme.onSurface
        )

        ContactDetailItem(
            iconPainter = painterResource(id = R.drawable.mailicon), // Reemplaza con tu drawable
            borderBrush = glowBrush,
            backgroundBrush = itemBackgroundGradient,
            label = "Email",
            value = "rmontufarm@unamad.edu.pe"
        )
        ContactDetailItem(
            iconPainter = painterResource(id = R.drawable.telephoneicon), // Reemplaza
            borderBrush = glowBrush,
            backgroundBrush = itemBackgroundGradient,
            label = "Teléfono",
            value = "983126035"
        )
        ContactDetailItem(
            iconPainter = painterResource(id = R.drawable.locationicon), // Reemplaza
            borderBrush = glowBrush,
            backgroundBrush = itemBackgroundGradient,
            label = "Ubicación",
            value = "Madre de Dios, Perú"
        )
    }
}

// @Composable // Función no utilizada eliminada
// fun ContactInfoItem(title: String, value: String) {
// Row(modifier = Modifier.padding(vertical = 4.dp)) {
// Text(
// text = "$title: ",
// style = MaterialTheme.typography.bodyLarge,
// fontWeight = FontWeight.SemiBold,
// color = MaterialTheme.colorScheme.onSurface // Asegura contraste
// )
// Text(
// text = value,
// style = MaterialTheme.typography.bodyLarge,
// color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.87f) // Contraste
// )
// }
// }


@Preview(showBackground = true, name = "HomeScreen Preview")
@Composable
fun HomeScreenPreview() {
    // Para el preview, podemos simular el tema de la app si tienes uno
    ProjectoFinalDetailedTheme {
    HomeScreen(
        onLoginClicked = { println("Login Clickeado") }, // Corregido
        onRegisterClicked = { println("Register Clickeado") } // Corregido
    )
        }
}
