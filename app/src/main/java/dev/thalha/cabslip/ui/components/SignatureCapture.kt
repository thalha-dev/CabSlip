package dev.thalha.cabslip.ui.components

import android.graphics.Bitmap
import android.graphics.Paint
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
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@Composable
fun SignatureCapture(
    modifier: Modifier = Modifier,
    onSignatureSaved: (String?) -> Unit = {},
    existingSignaturePath: String? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    var paths by remember { mutableStateOf(listOf<Path>()) }
    var currentPath by remember { mutableStateOf(Path()) }
    var hasSignature by remember { mutableStateOf(false) }
    var isDrawing by remember { mutableStateOf(false) }
    var existingSignatureBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Load existing signature if available
    LaunchedEffect(existingSignaturePath) {
        if (!existingSignaturePath.isNullOrBlank()) {
            val file = File(existingSignaturePath)
            if (file.exists()) {
                try {
                    val bitmap = android.graphics.BitmapFactory.decodeFile(existingSignaturePath)
                    if (bitmap != null) {
                        existingSignatureBitmap = bitmap
                        hasSignature = true
                    }
                } catch (e: Exception) {
                    existingSignatureBitmap = null
                }
            }
        } else {
            existingSignatureBitmap = null
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
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    // Clear existing signature when starting to draw new one
                                    existingSignatureBitmap = null
                                    currentPath = Path().apply {
                                        moveTo(offset.x, offset.y)
                                    }
                                    isDrawing = true
                                },
                                onDragEnd = {
                                    if (isDrawing) {
                                        paths = paths + currentPath
                                        hasSignature = true
                                        isDrawing = false
                                    }
                                }
                            ) { _, dragAmount ->
                                if (isDrawing) {
                                    currentPath.relativeLineTo(dragAmount.x, dragAmount.y)
                                }
                            }
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

                    // Draw completed paths (new signature being drawn)
                    paths.forEach { path ->
                        drawPath(
                            path = path,
                            color = Color.Black,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    // Draw current path being drawn
                    if (isDrawing) {
                        drawPath(
                            path = currentPath,
                            color = Color.Black,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }

                if (!hasSignature && !isDrawing && existingSignatureBitmap == null) {
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    paths = emptyList()
                    currentPath = Path()
                    hasSignature = false
                    isDrawing = false
                    existingSignatureBitmap = null
                    onSignatureSaved(null)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear")
            }

            Button(
                onClick = {
                    if (hasSignature) {
                        scope.launch {
                            try {
                                val signaturePath = if (paths.isNotEmpty()) {
                                    // Save new drawn signature
                                    saveSignatureToPng(context, paths, density)
                                } else {
                                    // Keep existing signature
                                    existingSignaturePath
                                }
                                onSignatureSaved(signaturePath)
                            } catch (e: Exception) {
                                onSignatureSaved(null)
                            }
                        }
                    } else {
                        onSignatureSaved(null)
                    }
                },
                enabled = hasSignature,
                modifier = Modifier.weight(1f)
            ) {
                Text("Save Signature")
            }
        }
    }
}

private suspend fun saveSignatureToPng(
    context: android.content.Context,
    paths: List<Path>,
    density: androidx.compose.ui.unit.Density
): String? {
    return try {
        val width = with(density) { 400.dp.toPx().toInt() }
        val height = with(density) { 200.dp.toPx().toInt() }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)

        // Fill with white background
        canvas.drawColor(android.graphics.Color.WHITE)

        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            strokeWidth = with(density) { 3.dp.toPx() }
            style = android.graphics.Paint.Style.STROKE
            strokeCap = android.graphics.Paint.Cap.ROUND
            isAntiAlias = true
        }

        // Convert Compose paths to Android paths and draw
        paths.forEach { composePath ->
            val androidPath = composePath.asAndroidPath()
            canvas.drawPath(androidPath, paint)
        }

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
    } catch (e: Exception) {
        null
    }
}
