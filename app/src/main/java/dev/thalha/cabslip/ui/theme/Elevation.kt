package dev.thalha.cabslip.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Professional Elevation System
object CabSlipElevation {
    val none = 0.dp
    val level1 = 1.dp  // For subtle cards
    val level2 = 3.dp  // For standard cards
    val level3 = 6.dp  // For raised elements
    val level4 = 8.dp  // For important content
    val level5 = 12.dp // For modal dialogs
    val level6 = 16.dp // For navigation drawers
    val level7 = 24.dp // For modal bottom sheets
}

// Custom elevation composables for consistent application
@Composable
fun ElevatedCard(
    modifier: Modifier = Modifier,
    elevation: Dp = CabSlipElevation.level2,
    shape: RoundedCornerShape = CabSlipShapes.cardElevated,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.shadow(elevation, shape),
        shape = shape,
        color = backgroundColor,
        contentColor = contentColor,
        content = content
    )
}

@Composable
fun OutlinedCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = CabSlipShapes.cardOutlined,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    borderWidth: Dp = CabSlipDimensions.Border.thin,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.border(
            BorderStroke(borderWidth, borderColor),
            shape
        ),
        shape = shape,
        color = backgroundColor,
        contentColor = contentColor,
        content = content
    )
}

@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    elevation: Dp = CabSlipElevation.level3,
    shape: RoundedCornerShape = CabSlipShapes.cardFilled,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(elevation, shape)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = shape,
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            content = {
                Box(modifier = Modifier.padding(CabSlipDimensions.Card.padding)) {
                    content()
                }
            }
        )
    }
}
