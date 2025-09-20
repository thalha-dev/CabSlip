package dev.thalha.cabslip.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CabInfoScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val database = CabSlipDatabase.getDatabase(context)
    val repository = CabSlipRepository(database.cabInfoDao(), database.receiptDao())

    val cabInfo by repository.getCabInfo().collectAsState(initial = null)

    var cabName by remember { mutableStateOf("") }
    var cabAddress by remember { mutableStateOf("") }
    var primaryContact by remember { mutableStateOf("") }
    var secondaryContact by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var logoPath by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Update form when cabInfo changes
    LaunchedEffect(cabInfo) {
        cabInfo?.let { info ->
            cabName = info.cabName
            cabAddress = info.cabAddress
            primaryContact = info.primaryContact
            secondaryContact = info.secondaryContact ?: ""
            email = info.email
            logoPath = info.logoPath
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Cab Information",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

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
            existingLogoPath = logoPath,
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

        if (showSuccessMessage) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Information updated successfully!",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (cabName.isBlank() || cabAddress.isBlank() ||
                    primaryContact.isBlank() || email.isBlank()) {
                    errorMessage = "Please fill in all required fields"
                    showSuccessMessage = false
                    return@Button
                }

                errorMessage = ""
                isLoading = true
                showSuccessMessage = false

                scope.launch {
                    try {
                        val currentTime = System.currentTimeMillis()
                        val updatedCabInfo = CabInfo(
                            cabName = cabName.trim(),
                            cabAddress = cabAddress.trim(),
                            primaryContact = primaryContact.trim(),
                            secondaryContact = if (secondaryContact.isBlank()) null else secondaryContact.trim(),
                            email = email.trim(),
                            logoPath = logoPath,
                            createdAt = cabInfo?.createdAt ?: currentTime,
                            updatedAt = currentTime
                        )

                        repository.insertOrUpdateCabInfo(updatedCabInfo)
                        showSuccessMessage = true
                    } catch (_: Exception) {
                        errorMessage = "Failed to save information. Please try again."
                    } finally {
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
                Text("Save Changes")
            }
        }
    }
}
