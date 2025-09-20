package dev.thalha.cabslip.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.thalha.cabslip.data.database.CabSlipDatabase
import dev.thalha.cabslip.data.entity.CabInfo
import dev.thalha.cabslip.data.repository.CabSlipRepository
import dev.thalha.cabslip.ui.components.LogoUpload
import kotlinx.coroutines.launch

@Composable
fun FirstTimeSetupScreen(
    onSetupComplete: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Initialize repository
    val database = CabSlipDatabase.getDatabase(context)
    val repository = CabSlipRepository(database.cabInfoDao(), database.receiptDao())

    var cabName by remember { mutableStateOf("") }
    var cabAddress by remember { mutableStateOf("") }
    var primaryContact by remember { mutableStateOf("") }
    var secondaryContact by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var logoPath by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Check if setup is already done
    LaunchedEffect(Unit) {
        val existingCabInfo = repository.getCabInfoSync()
        if (existingCabInfo != null) {
            onSetupComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Welcome to CabSlip",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Let's set up your cab information",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = cabName,
            onValueChange = { cabName = it },
            label = { Text("Cab Name *") },
            modifier = Modifier.fillMaxWidth(),
            isError = cabName.isBlank() && errorMessage.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = cabAddress,
            onValueChange = { cabAddress = it },
            label = { Text("Address *") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            isError = cabAddress.isBlank() && errorMessage.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = primaryContact,
            onValueChange = { primaryContact = it },
            label = { Text("Primary Contact *") },
            modifier = Modifier.fillMaxWidth(),
            isError = primaryContact.isBlank() && errorMessage.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = secondaryContact,
            onValueChange = { secondaryContact = it },
            label = { Text("Secondary Contact (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email *") },
            modifier = Modifier.fillMaxWidth(),
            isError = email.isBlank() && errorMessage.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Logo Upload Section - NEW
        LogoUpload(
            onLogoSelected = { path ->
                logoPath = path
            }
        )

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (cabName.isBlank() || cabAddress.isBlank() ||
                    primaryContact.isBlank() || email.isBlank()) {
                    errorMessage = "Please fill in all required fields"
                    return@Button
                }

                errorMessage = ""
                isLoading = true

                scope.launch {
                    try {
                        val currentTime = System.currentTimeMillis()
                        val cabInfo = CabInfo(
                            cabName = cabName.trim(),
                            cabAddress = cabAddress.trim(),
                            primaryContact = primaryContact.trim(),
                            secondaryContact = if (secondaryContact.isBlank()) null else secondaryContact.trim(),
                            email = email.trim(),
                            logoPath = logoPath,
                            createdAt = currentTime,
                            updatedAt = currentTime
                        )

                        repository.insertOrUpdateCabInfo(cabInfo)
                        onSetupComplete()
                    } catch (_: Exception) {
                        errorMessage = "Failed to save information. Please try again."
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Complete Setup")
            }
        }

        // Add extra bottom padding for better spacing
        Spacer(modifier = Modifier.height(16.dp))
    }
}
