package com.example.projectofinal.ui.theme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope // Necesario si quieres que el lambda de contenido sea RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.projectofinal.ui.theme.* // Asume que DarkOnPrimary está aquí

@Composable
fun GradientButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradientBrush: Brush,
    shape: Shape = RoundedCornerShape(8.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    // Quita textColor y textStyle de esta firma, ya que el contenido los define
    content: @Composable () -> Unit // Simplificado a @Composable () -> Unit si no necesitas RowScope
    // content: @Composable RowScope.() -> Unit // Si necesitas RowScope para el contenido
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(gradientBrush)
            .clickable(onClick = onClick)
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        content() // El contenido (que incluye el Text con su propio color y estilo) se renderiza aquí
    }
}

/**
 * Sobrecarga de [GradientButton] que toma un String simple para el texto.
 */
@Composable
fun GradientButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    gradientBrush: Brush,
    shape: Shape = RoundedCornerShape(8.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    textColor: Color = DarkOnPrimary, // Color para ESTE Text específico
    textStyle: TextStyle = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold) // Estilo para ESTE Text
) {
    GradientButton( // Llama a la primera sobrecarga
        onClick = onClick,
        modifier = modifier,
        gradientBrush = gradientBrush,
        shape = shape,
        contentPadding = contentPadding
    ) {
        // Aquí es donde se define el Text y se aplican textColor y textStyle
        Text(
            text = text,
            style = textStyle,
            color = textColor
        )
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF0A1929)
@Composable
fun GradientButtonPreview() {
    ProjectoFinalDetailedTheme {
        val exampleBrush = Brush.horizontalGradient(
            colors = listOf(GradientBlueStart, GradientCyanEnd)
        )
        GradientButton(
            onClick = { /* Acción de ejemplo */ },
            text = "INICIAR SESIÓN",
            gradientBrush = exampleBrush,
            textColor = DarkOnPrimary, // Prueba esto en el preview también
            modifier = Modifier.padding(16.dp)
        )
    }
}