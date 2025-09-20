package dev.thalha.cabslip.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@Composable
fun LogoUpload(
    modifier: Modifier = Modifier,
    existingLogoPath: String? = null,
    onLogoSelected: (String?) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var logoPath by remember { mutableStateOf(existingLogoPath) }
    var logoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Load existing logo
    LaunchedEffect(existingLogoPath) {
        logoPath = existingLogoPath
        if (!existingLogoPath.isNullOrBlank()) {
            try {
                val file = File(existingLogoPath)
                if (file.exists()) {
                    logoBitmap = BitmapFactory.decodeFile(existingLogoPath)
                }
            } catch (e: Exception) {
                logoBitmap = null
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                try {
                    isUploading = true
                    errorMessage = ""

                    val savedPath = processAndSaveLogo(context, uri)
                    if (savedPath != null) {
                        logoPath = savedPath
                        logoBitmap = BitmapFactory.decodeFile(savedPath)
                        onLogoSelected(savedPath)
                    } else {
                        errorMessage = "Failed to process logo. Please try again."
                    }
                } catch (e: Exception) {
                    errorMessage = "Invalid image file. Please select a valid PNG or JPEG image."
                } finally {
                    isUploading = false
                }
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Cab Logo",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isUploading -> {
                        CircularProgressIndicator()
                    }
                    logoBitmap != null -> {
                        // Show uploaded logo
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Image(
                                bitmap = logoBitmap!!.asImageBitmap(),
                                contentDescription = "Cab Logo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Fit
                            )

                            // Delete button
                            IconButton(
                                onClick = {
                                    logoPath?.let { path ->
                                        try {
                                            File(path).delete()
                                        } catch (e: Exception) {
                                            // Ignore deletion errors
                                        }
                                    }
                                    logoPath = null
                                    logoBitmap = null
                                    onLogoSelected(null)
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(
                                        MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                        RoundedCornerShape(16.dp)
                                    )
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Remove Logo",
                                    tint = MaterialTheme.colorScheme.onError
                                )
                            }
                        }
                    }
                    else -> {
                        // Upload area
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { imagePickerLauncher.launch("image/*") }
                                .border(
                                    2.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add Logo",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap to upload cab logo",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "PNG/JPEG • Max 200KB • 512x512px recommended",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }

        if (logoBitmap != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors()
            ) {
                Text("Change Logo")
            }
        }
    }
}

private suspend fun processAndSaveLogo(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        if (originalBitmap == null) return null

        // Validate image dimensions and file size
        val maxSizeKB = 200
        val recommendedSize = 512

        // Scale down if too large
        val scaledBitmap = if (originalBitmap.width > recommendedSize || originalBitmap.height > recommendedSize) {
            val scale = minOf(
                recommendedSize.toFloat() / originalBitmap.width,
                recommendedSize.toFloat() / originalBitmap.height
            )
            val newWidth = (originalBitmap.width * scale).toInt()
            val newHeight = (originalBitmap.height * scale).toInt()
            Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
        } else {
            originalBitmap
        }

        // Save to internal storage
        val logoDir = File(context.filesDir, "logos")
        if (!logoDir.exists()) {
            logoDir.mkdirs()
        }

        val timestamp = System.currentTimeMillis()
        val logoFile = File(logoDir, "logo_$timestamp.png")

        FileOutputStream(logoFile).use { out ->
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
        }

        // Check file size
        val fileSizeKB = logoFile.length() / 1024
        if (fileSizeKB > maxSizeKB) {
            // Try with lower quality
            FileOutputStream(logoFile).use { out ->
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, out)
            }

            val newFileSizeKB = logoFile.length() / 1024
            if (newFileSizeKB > maxSizeKB) {
                logoFile.delete()
                return null
            }
        }

        // Clean up
        if (scaledBitmap != originalBitmap) {
            originalBitmap.recycle()
        }

        logoFile.absolutePath
    } catch (e: Exception) {
        null
    }
}
