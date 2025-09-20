package dev.thalha.cabslip.ui.components

import android.graphics.Bitmap
import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.io.FileOutputStream

@Composable
fun SignatureCapture(
    modifier: Modifier = Modifier,
    onSignatureChanged: (Path?, Boolean, String?) -> Unit = { _, _, _ -> },
    existingSignaturePath: String? = null
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    val path by remember { mutableStateOf(Path()) }
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    var hasExistingSignature by remember { mutableStateOf(false) }
    var hasNewlyDrawnSignature by remember { mutableStateOf(false) }
    var existingSignatureBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Load existing signature if available
    LaunchedEffect(existingSignaturePath) {
        Log.d("SignatureCapture", "Loading existing signature: $existingSignaturePath")
        if (!existingSignaturePath.isNullOrBlank()) {
            val file = File(existingSignaturePath)
            if (file.exists()) {
                try {
                    val bitmap = android.graphics.BitmapFactory.decodeFile(existingSignaturePath)
                    if (bitmap != null) {
                        existingSignatureBitmap = bitmap
                        hasExistingSignature = true
                        hasNewlyDrawnSignature = false
                        Log.d("SignatureCapture", "Loaded existing signature, notifying parent")
                        // Notify parent of existing signature - NOT a new one
                        onSignatureChanged(null, false, existingSignaturePath)
                    }
                } catch (_: Exception) {
                    existingSignatureBitmap = null
                    hasExistingSignature = false
                }
            }
        } else {
            existingSignatureBitmap = null
            hasExistingSignature = false
        }
    }

    // Only notify about NEW signatures when actually drawn
    LaunchedEffect(hasNewlyDrawnSignature, path) {
        if (hasNewlyDrawnSignature && !path.isEmpty) {
            Log.d("SignatureCapture", "Notifying parent: NEW signature drawn")
            onSignatureChanged(path, true, null)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Owner Signature",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            RoundedCornerShape(8.dp)
                        )
                        .pointerInput(true) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    Log.d("SignatureCapture", "onDragStart - user drawing NEW signature")
                                    // Clear existing signature when starting to draw new one
                                    existingSignatureBitmap = null
                                    hasExistingSignature = false
                                    path.reset()
                                    path.moveTo(offset.x, offset.y)
                                    currentPosition = offset
                                    hasNewlyDrawnSignature = true
                                    Log.d("SignatureCapture", "hasNewlyDrawnSignature set to true")
                                },
                                onDrag = { change, _ ->
                                    path.lineTo(change.position.x, change.position.y)
                                    currentPosition = change.position
                                }
                            )
                        }
                ) {
                    // Draw existing signature bitmap if available
                    existingSignatureBitmap?.let { bitmap ->
                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        val bitmapWidth = bitmap.width.toFloat()
                        val bitmapHeight = bitmap.height.toFloat()

                        // Scale to fit canvas while maintaining aspect ratio
                        val scale = minOf(canvasWidth / bitmapWidth, canvasHeight / bitmapHeight)
                        val scaledWidth = bitmapWidth * scale
                        val scaledHeight = bitmapHeight * scale

                        // Center the image
                        val offsetX = (canvasWidth - scaledWidth) / 2
                        val offsetY = (canvasHeight - scaledHeight) / 2

                        drawImage(
                            image = bitmap.asImageBitmap(),
                            dstOffset = androidx.compose.ui.unit.IntOffset(offsetX.toInt(), offsetY.toInt()),
                            dstSize = androidx.compose.ui.unit.IntSize(scaledWidth.toInt(), scaledHeight.toInt())
                        )
                    }

                    // Draw the signature path in real-time (for newly drawn signatures)
                    if (currentPosition != Offset.Unspecified && !path.isEmpty && hasNewlyDrawnSignature) {
                        drawPath(
                            path = path,
                            color = Color.Black,
                            style = Stroke(
                                width = 3.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }

                if (!hasExistingSignature && !hasNewlyDrawnSignature && currentPosition == Offset.Unspecified && existingSignatureBitmap == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Draw your signature here",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Only show Clear button
        if (hasExistingSignature || hasNewlyDrawnSignature || existingSignatureBitmap != null) {
            OutlinedButton(
                onClick = {
                    Log.d("SignatureCapture", "Clear button clicked")
                    path.reset()
                    currentPosition = Offset.Unspecified
                    hasExistingSignature = false
                    hasNewlyDrawnSignature = false
                    existingSignatureBitmap = null
                    onSignatureChanged(null, false, null)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear Signature")
            }
        }
    }
}

// Function to save signature path when needed
fun saveSignatureFromPath(
    context: android.content.Context,
    path: Path?,
    density: androidx.compose.ui.unit.Density
): String? {
    return if (path != null && !path.isEmpty) {
        saveSignatureToPng(context, path, density)
    } else {
        null
    }
}

private fun saveSignatureToPng(
    context: android.content.Context,
    path: Path,
    density: androidx.compose.ui.unit.Density
): String? {
    return try {
        val width = with(density) { 400.dp.toPx().toInt() }
        val height = with(density) { 200.dp.toPx().toInt() }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)

        // Fill with white background
        canvas.drawColor(android.graphics.Color.WHITE)

        val paint = Paint().apply {
            color = android.graphics.Color.BLACK
            strokeWidth = with(density) { 3.dp.toPx() }
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            isAntiAlias = true
        }

        // Convert Compose path to Android path and draw
        val androidPath = path.asAndroidPath()
        canvas.drawPath(androidPath, paint)

        // Save to internal storage
        val signatureDir = File(context.filesDir, "signatures")
        if (!signatureDir.exists()) {
            signatureDir.mkdirs()
        }

        val timestamp = System.currentTimeMillis()
        val signatureFile = File(signatureDir, "signature_$timestamp.png")

        FileOutputStream(signatureFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        signatureFile.absolutePath
    } catch (_: Exception) {
        null
    }
}
