package dev.thalha.cabslip.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    // Primary colors - Deep Navy for professional appearance
    primary = NavyBlue600,
    onPrimary = PureWhite,
    primaryContainer = NavyBlue100,
    onPrimaryContainer = NavyBlue900,

    // Secondary colors - Elegant Gold accents
    secondary = Gold600,
    onSecondary = NavyBlue900,
    secondaryContainer = Gold100,
    onSecondaryContainer = Gold600,

    // Tertiary colors - Warm Copper for highlights
    tertiary = Copper600,
    onTertiary = PureWhite,
    tertiaryContainer = Gold200,
    onTertiaryContainer = Copper600,

    // Error colors
    error = Error500,
    onError = PureWhite,
    errorContainer = Error100,
    onErrorContainer = Error700,

    // Background and Surface colors
    background = PureWhite,
    onBackground = Gray900,
    surface = PureWhite,
    onSurface = Gray900,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = Gray700,
    surfaceTint = NavyBlue600,

    // Outline colors
    outline = Gray300,
    outlineVariant = Gray200,

    // Container colors
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = Gray100,
    surfaceContainerLow = SurfaceTint,
    surfaceContainerLowest = PureWhite,

    // Inverse colors
    inverseSurface = NavyBlue800,
    inverseOnSurface = NavyBlue50,
    inversePrimary = NavyBlue200,

    // Scrim
    scrim = NavyBlue900
)

private val DarkColorScheme = darkColorScheme(
    // Primary colors - Lighter navy for dark mode
    primary = NavyBlue300,
    onPrimary = NavyBlue900,
    primaryContainer = NavyBlue700,
    onPrimaryContainer = NavyBlue100,

    // Secondary colors - Brighter gold for dark mode
    secondary = Gold400,
    onSecondary = NavyBlue900,
    secondaryContainer = Gold600,
    onSecondaryContainer = Gold100,

    // Tertiary colors
    tertiary = Copper400,
    onTertiary = NavyBlue900,
    tertiaryContainer = Copper600,
    onTertiaryContainer = Gold100,

    // Error colors
    error = Error500,
    onError = Error100,
    errorContainer = Error700,
    onErrorContainer = Error100,

    // Background and Surface colors
    background = NavyBlue900,
    onBackground = NavyBlue50,
    surface = NavyBlue900,
    onSurface = NavyBlue50,
    surfaceVariant = NavyBlue800,
    onSurfaceVariant = NavyBlue200,
    surfaceTint = NavyBlue300,

    // Outline colors
    outline = NavyBlue400,
    outlineVariant = NavyBlue600,

    // Container colors
    surfaceContainer = NavyBlue800,
    surfaceContainerHigh = NavyBlue700,
    surfaceContainerHighest = NavyBlue600,
    surfaceContainerLow = NavyBlue800,
    surfaceContainerLowest = NavyBlue900,

    // Inverse colors
    inverseSurface = NavyBlue50,
    inverseOnSurface = NavyBlue800,
    inversePrimary = NavyBlue600,

    // Scrim
    scrim = PureBlack
)

@Composable
fun CabSlipTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is disabled for consistent branding
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}