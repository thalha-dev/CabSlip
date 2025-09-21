package dev.thalha.cabslip.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

/**
 * CabSlip Professional Theme Usage Examples
 *
 * This file demonstrates how to use the new premium theme components
 * for consistent and professional UI throughout the app.
 */

@Preview(showBackground = true)
@Composable
fun ThemeShowcasePreview() {
    CabSlipThemePreview {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(CabSlipDimensions.screenHorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(CabSlipDimensions.spaceLG)
        ) {
            // App Title with premium typography
            Text(
                text = "CabSlip Professional",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Premium Receipt Card Example
            CabSlipThemeExtensions.ReceiptCard {
                Text(
                    text = "Trip Receipt #CS001",
                    style = CabSlipTypography.receiptTitle
                )
                Text(
                    text = "Downtown → Airport",
                    style = CabSlipTypography.receiptSubtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "₹450.00",
                    style = CabSlipTypography.receiptAmount,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // Professional Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(CabSlipDimensions.spaceMD)
            ) {
                CabSlipThemeExtensions.PrimaryActionButton(
                    onClick = { },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Create Receipt", style = CabSlipTypography.buttonText)
                }

                CabSlipThemeExtensions.SecondaryActionButton(
                    onClick = { },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("View All", style = CabSlipTypography.buttonText)
                }
            }

            // Status Indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(CabSlipDimensions.spaceSM)
            ) {
                CabSlipThemeExtensions.StatusIndicator(IndicatorStatus.Success)
                CabSlipThemeExtensions.StatusIndicator(IndicatorStatus.Warning)
                CabSlipThemeExtensions.StatusIndicator(IndicatorStatus.Error)
                CabSlipThemeExtensions.StatusIndicator(IndicatorStatus.Info)
            }
        }
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ThemeShowcaseDarkPreview() {
    CabSlipThemePreview(darkTheme = true) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(CabSlipDimensions.screenHorizontalPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Professional Dark Theme",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(CabSlipDimensions.spaceXL))

            CabSlipThemeExtensions.PremiumCard {
                Text(
                    text = "Premium Experience",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Sophisticated design meets professional functionality",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Quick Reference for Theme Usage:
 *
 * Colors:
 * - Primary: MaterialTheme.colorScheme.primary (Navy Blue)
 * - Secondary: MaterialTheme.colorScheme.secondary (Gold)
 * - Surface: MaterialTheme.colorScheme.surface
 * - Background: MaterialTheme.colorScheme.background
 *
 * Typography:
 * - Headlines: MaterialTheme.typography.headlineLarge/Medium/Small
 * - Body: MaterialTheme.typography.bodyLarge/Medium/Small
 * - Titles: MaterialTheme.typography.titleLarge/Medium/Small
 * - Custom: CabSlipTypography.receiptTitle/Amount/Details
 *
 * Spacing:
 * - Use CabSlipDimensions.spaceSM/MD/LG/XL for consistent spacing
 * - Screen padding: CabSlipDimensions.screenHorizontalPadding
 * - Component spacing: CabSlipDimensions.componentSpacing
 *
 * Shapes:
 * - Cards: CabSlipShapes.cardElevated/Outlined
 * - Buttons: CabSlipShapes.buttonMedium/Large
 * - Dialogs: CabSlipShapes.dialog/alertDialog
 *
 * Components:
 * - Use CabSlipThemeExtensions.PremiumCard for elevated content
 * - Use CabSlipThemeExtensions.ReceiptCard for receipt displays
 * - Use CabSlipThemeExtensions.PrimaryActionButton for main actions
 */
