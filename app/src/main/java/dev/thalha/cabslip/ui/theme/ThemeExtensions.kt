package dev.thalha.cabslip.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Professional Theme Extensions and Utilities
object CabSlipThemeExtensions {

    // Premium gradient backgrounds
    @Composable
    fun primaryGradient() = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primaryContainer
        )
    )

    @Composable
    fun secondaryGradient() = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.secondaryContainer
        )
    )

    @Composable
    fun surfaceGradient() = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surfaceVariant
        )
    )

    @Composable
    fun errorGradient() = Brush.verticalGradient(
        colors = listOf(
            Error500,
            Error100
        )
    )

    @Composable
    fun successGradient() = Brush.verticalGradient(
        colors = listOf(
            Success500,
            Success100
        )
    )

    // Premium card styles
    @Composable
    fun PremiumCard(
        modifier: Modifier = Modifier,
        onClick: (() -> Unit)? = null,
        content: @Composable ColumnScope.() -> Unit
    ) {
        val cardModifier = if (onClick != null) {
            modifier.then(
                Modifier
                    .clip(CabSlipShapes.cardElevated)
                    .background(surfaceGradient())
            )
        } else {
            modifier
        }

        ElevatedCard(
            modifier = cardModifier,
            elevation = CabSlipElevation.level3,
            shape = CabSlipShapes.cardElevated
        ) {
            Column(
                modifier = Modifier.padding(CabSlipDimensions.Card.paddingLarge),
                content = content
            )
        }
    }

    @Composable
    fun ReceiptCard(
        modifier: Modifier = Modifier,
        content: @Composable ColumnScope.() -> Unit
    ) {
        ElevatedCard(
            modifier = modifier,
            elevation = CabSlipElevation.level2,
            shape = CabSlipShapes.receiptPreview,
            backgroundColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                modifier = Modifier.padding(CabSlipDimensions.Receipt.cardPadding),
                verticalArrangement = Arrangement.spacedBy(CabSlipDimensions.Receipt.itemSpacing),
                content = content
            )
        }
    }

    // Status indicators with semantic colors
    @Composable
    fun StatusIndicator(
        status: IndicatorStatus,
        modifier: Modifier = Modifier
    ) {
        val (backgroundColor, contentColor) = when (status) {
            IndicatorStatus.Success -> Success100 to Success700
            IndicatorStatus.Warning -> Warning100 to Warning700
            IndicatorStatus.Error -> Error100 to Error700
            IndicatorStatus.Info -> Info100 to Info700
            IndicatorStatus.Neutral -> Gray100 to Gray700
        }

        Surface(
            modifier = modifier,
            shape = CabSlipShapes.badge,
            color = backgroundColor
        ) {
            Box(
                modifier = Modifier.padding(
                    horizontal = CabSlipDimensions.spaceMD,
                    vertical = CabSlipDimensions.spaceXS
                )
            ) {
                // Status content can be added here
            }
        }
    }

    // Professional button styles
    @Composable
    fun PrimaryActionButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        content: @Composable RowScope.() -> Unit
    ) {
        Button(
            onClick = onClick,
            modifier = modifier.height(CabSlipDimensions.Button.heightLarge),
            enabled = enabled,
            shape = CabSlipShapes.buttonLarge,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = CabSlipElevation.level2,
                pressedElevation = CabSlipElevation.level1
            ),
            content = content
        )
    }

    @Composable
    fun SecondaryActionButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        content: @Composable RowScope.() -> Unit
    ) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(CabSlipDimensions.Button.heightLarge),
            enabled = enabled,
            shape = CabSlipShapes.buttonLarge,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = CabSlipDimensions.Border.medium
            ),
            content = content
        )
    }

    @Composable
    fun FloatingActionButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {
        androidx.compose.material3.FloatingActionButton(
            onClick = onClick,
            modifier = modifier,
            shape = CabSlipShapes.buttonPill,
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = CabSlipElevation.level4,
                pressedElevation = CabSlipElevation.level2
            ),
            content = content
        )
    }
}

// Status indicator types
enum class IndicatorStatus {
    Success, Warning, Error, Info, Neutral
}

// Theme preview helpers for development
@Composable
fun CabSlipThemePreview(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    CabSlipTheme(darkTheme = darkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}
