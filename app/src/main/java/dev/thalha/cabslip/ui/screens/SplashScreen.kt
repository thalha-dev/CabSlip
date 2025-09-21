package dev.thalha.cabslip.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.thalha.cabslip.R
import dev.thalha.cabslip.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }

    // Single, smooth logo animation
    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )

    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1500,
            easing = FastOutSlowInEasing
        ),
        label = "logoAlpha"
    )

    // Clean solid background matching your brand
    val backgroundColor = Gray100 // Light gray background for dark logo

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500) // Clean 2.5 second splash
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor), // Light gray background
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Your new dark logo
            Image(
                painter = painterResource(id = R.drawable.cabsliplogo), // Updated to use your new logo
                contentDescription = "CabSlip Logo",
                modifier = Modifier
                    .size(140.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Clean app name - adjusted color for light background
            Text(
                text = "CabSlip",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                ),
                color = NavyBlue700, // Darker color for light background
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(logoAlpha)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Simple tagline - adjusted color for light background
            Text(
                text = "Professional Receipt Management",
                style = MaterialTheme.typography.bodyLarge,
                color = NavyBlue600, // Darker color for light background
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(logoAlpha)
            )
        }
    }
}
