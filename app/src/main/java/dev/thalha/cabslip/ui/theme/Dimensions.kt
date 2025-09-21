package dev.thalha.cabslip.ui.theme

import androidx.compose.ui.unit.dp

// Professional Spacing System
object CabSlipDimensions {
    // Base spacing units
    val spaceXXS = 2.dp
    val spaceXS = 4.dp
    val spaceSM = 8.dp
    val spaceMD = 12.dp
    val spaceLG = 16.dp
    val spaceXL = 20.dp
    val spaceXXL = 24.dp
    val space3XL = 32.dp
    val space4XL = 40.dp
    val space5XL = 48.dp
    val space6XL = 64.dp

    // Screen margins and padding
    val screenHorizontalPadding = 20.dp
    val screenVerticalPadding = 16.dp
    val sectionSpacing = 24.dp
    val componentSpacing = 16.dp

    // Component specific dimensions
    object Button {
        val heightSmall = 32.dp
        val heightMedium = 40.dp
        val heightLarge = 48.dp
        val heightXLarge = 56.dp
        val paddingHorizontal = 16.dp
        val paddingVertical = 8.dp
        val minWidth = 64.dp
    }

    object Card {
        val padding = 16.dp
        val paddingLarge = 20.dp
        val elevation = 4.dp
        val elevationHover = 8.dp
        val minHeight = 80.dp
    }

    object TextField {
        val height = 56.dp
        val paddingHorizontal = 16.dp
        val paddingVertical = 12.dp
        val labelSpacing = 8.dp
    }

    object AppBar {
        val height = 64.dp
        val padding = 16.dp
        val iconSize = 24.dp
        val elevation = 4.dp
    }

    object BottomNavigation {
        val height = 80.dp
        val iconSize = 24.dp
        val labelSpacing = 4.dp
        val itemPadding = 8.dp
    }

    object Receipt {
        val cardPadding = 20.dp
        val itemSpacing = 12.dp
        val sectionSpacing = 20.dp
        val signatureHeight = 120.dp
        val logoSize = 64.dp
    }

    object Dialog {
        val padding = 24.dp
        val titleSpacing = 16.dp
        val actionSpacing = 8.dp
        val minWidth = 280.dp
        val maxWidth = 560.dp
    }

    // Icon sizes
    object Icons {
        val small = 16.dp
        val medium = 24.dp
        val large = 32.dp
        val extraLarge = 48.dp
    }

    // Avatar and image sizes
    object Avatar {
        val small = 32.dp
        val medium = 40.dp
        val large = 48.dp
        val extraLarge = 64.dp
    }

    // Border widths
    object Border {
        val thin = 1.dp
        val medium = 2.dp
        val thick = 4.dp
    }

    // Corner radius variations
    object CornerRadius {
        val none = 0.dp
        val small = 4.dp
        val medium = 8.dp
        val large = 12.dp
        val extraLarge = 16.dp
        val round = 50.dp
    }
}
