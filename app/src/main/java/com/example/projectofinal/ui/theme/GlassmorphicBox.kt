package com.example.projectofinal.ui.common // O tu paquete

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*       // Clave: Asegúrate de que graphics.* esté aquí
// import androidx.compose.ui.graphics.RenderEffect NO necesitas esta importación específica
// import androidx.compose.ui.graphics.Shader NO necesitas esta importación específica
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GlassmorphicBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.25f),
    blurRadius: Dp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) 16.dp else 0.dp,
    borderWidth: Dp = 1.dp,
    borderColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
    contentPadding: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            /*.then(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && blurRadius > 0.dp) {
                    Modifier.graphicsLayer {
                        this.renderEffect = BlurEffect(
                            radiusX = blurRadius.toPx(),
                            radiusY = blurRadius.toPx(),
                            edgeTreatment = TileMode.Decal
                        )
                        this.clip = true
                    }
                } else {
                    Modifier
                }
            )*/
            .background(backgroundColor)
            .padding(contentPadding)
    ) {
        content()
    }
}
