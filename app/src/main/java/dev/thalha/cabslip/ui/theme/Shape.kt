package dev.thalha.cabslip.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Professional Shape System with refined corner radiuses
val Shapes = Shapes(
    // Extra small - For buttons, chips, and small elements
    extraSmall = RoundedCornerShape(4.dp),

    // Small - For cards, dialogs, and medium components
    small = RoundedCornerShape(8.dp),

    // Medium - For larger cards, bottom sheets, and prominent elements
    medium = RoundedCornerShape(12.dp),

    // Large - For modal dialogs, screens, and major containers
    large = RoundedCornerShape(16.dp),

    // Extra large - For full screen modals and hero elements
    extraLarge = RoundedCornerShape(24.dp)
)

// Extended shapes for specific components
object CabSlipShapes {
    // Button shapes
    val buttonSmall = RoundedCornerShape(6.dp)
    val buttonMedium = RoundedCornerShape(8.dp)
    val buttonLarge = RoundedCornerShape(12.dp)
    val buttonPill = RoundedCornerShape(50.dp)

    // Card shapes
    val cardElevated = RoundedCornerShape(12.dp)
    val cardOutlined = RoundedCornerShape(8.dp)
    val cardFilled = RoundedCornerShape(16.dp)

    // Input field shapes
    val textFieldOutlined = RoundedCornerShape(8.dp)
    val textFieldFilled = RoundedCornerShape(
        topStart = 8.dp,
        topEnd = 8.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    // Navigation shapes
    val bottomNavigation = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    val navigationRail = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 16.dp,
        bottomStart = 0.dp,
        bottomEnd = 16.dp
    )

    // Modal and dialog shapes
    val modalBottomSheet = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 20.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    val dialog = RoundedCornerShape(20.dp)
    val alertDialog = RoundedCornerShape(16.dp)

    // Signature and special components
    val signatureCanvas = RoundedCornerShape(12.dp)
    val imageContainer = RoundedCornerShape(8.dp)
    val receiptPreview = RoundedCornerShape(16.dp)

    // Badge and indicator shapes
    val badge = RoundedCornerShape(50.dp)
    val indicator = RoundedCornerShape(2.dp)
    val progressIndicator = RoundedCornerShape(4.dp)
}
