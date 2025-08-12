package com.example.projectofinal.ui.auth

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.projectofinal.R

// Data class to hold information for each navigation card
data class NavigationItem(
    val title: String,
    val description: String,
    @DrawableRes val iconResId: Int,
    val onClick: () -> Unit
)

@Composable
fun WelcomeScreen(
    userName: String?,
    onNavigateToUpload: () -> Unit,
    onNavigateToImages: () -> Unit,
    onNavigateToVideos: () -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navigationItems = listOf(
        NavigationItem("Subir Archivos", "Inicia un nuevo proceso", R.drawable.nubeicon, onNavigateToUpload),
        NavigationItem("Imágenes", "Revisa tus resultados", R.drawable.imageicon, onNavigateToImages),
        NavigationItem("Videos", "Explora tus creaciones", R.drawable.videoicon, onNavigateToVideos),
        NavigationItem("Historial", "Consulta tus procesos", R.drawable.historyicon, onNavigateToHistory)
    )

    // Fondo con degradado
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(brush = backgroundBrush)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenido${userName?.let { ", $it" } ?: ""}!",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(vertical = 32.dp),
            textAlign = TextAlign.Center
        )

        ImageCompareSlider(
            originalImageRes = R.drawable.demo,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(16.dp))
        )
        Spacer(modifier = Modifier.height(32.dp))


        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // Rejilla de 2 columnas
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(navigationItems) { item ->
                NavigationCard(
                    title = item.title,
                    description = item.description,
                    iconResId = item.iconResId,
                    onClick = item.onClick
                )
            }
        }
    }
}

@Composable
fun ImageCompareSlider(
    @DrawableRes originalImageRes: Int,
    modifier: Modifier = Modifier
) {
    var sliderPosition by remember { mutableStateOf(0.5f) }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, _ ->
                    sliderPosition = (change.position.x / size.width).coerceIn(0f, 1f)
                }
            }
    ) {
        // Blurred Image
        Image(
            painter = painterResource(id = originalImageRes),
            contentDescription = "Original",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    // El RenderEffect para desenfoque solo está disponible en Android S (API 31) y superior
                    renderEffect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        // Creamos el efecto de desenfoque y lo convertimos al tipo que espera Compose.
                        RenderEffect
                            .createBlurEffect(30f, 30f, Shader.TileMode.DECAL)
                            .asComposeRenderEffect()
                    } else {
                        // En versiones anteriores, no aplicamos ningún efecto.
                        null
                    }
                )
        )

        // Clear Image
        Image(
            painter = painterResource(id = originalImageRes),
            contentDescription = "Clear",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    val clipRect = Rect(
                        left = 0f,
                        top = 0f,
                        right = size.width * sliderPosition,
                        bottom = size.height
                    )
                    drawContent()
                    // Apply a darkening effect to the blurred part
                    drawRect(
                        color = Color.Black.copy(alpha = 0.3f),
                        topLeft = clipRect.topRight.let { androidx.compose.ui.geometry.Offset(it.x, it.y) },
                        size = androidx.compose.ui.geometry.Size(size.width * (1 - sliderPosition), size.height),
                        blendMode = BlendMode.Darken
                    )
                }
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen) // Required for clipping
                .clip(RoundedCornerShape(16.dp)) // Re-apply clipping after drawing
        )
    }
}


@Composable
fun NavigationCard(
    title: String,
    description: String,
    @DrawableRes iconResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f) // Hace la tarjeta cuadrada
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = title,
                modifier = Modifier
                    .size(48.dp)
                    .padding(bottom = 8.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
