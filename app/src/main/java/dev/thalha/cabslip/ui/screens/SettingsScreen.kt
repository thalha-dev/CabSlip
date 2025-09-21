package dev.thalha.cabslip.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.thalha.cabslip.R
import dev.thalha.cabslip.data.database.CabSlipDatabase
import dev.thalha.cabslip.data.repository.CabSlipRepository
import dev.thalha.cabslip.utils.BackupRestoreUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = CabSlipDatabase.getDatabase(context)
    val repository = CabSlipRepository(database.cabInfoDao(), database.receiptDao())
    val backupUtils = remember { BackupRestoreUtils(context, repository) }

    var isExporting by remember { mutableStateOf(false) }
    var isImporting by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    var showConfirmRestoreDialog by remember { mutableStateOf(false) }
    var pendingRestoreUri by remember { mutableStateOf<Uri?>(null) }

    // Export backup launcher
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            scope.launch {
                isExporting = true
                try {
                    val result = backupUtils.exportBackupToUri(it)
                    result.fold(
                        onSuccess = {
                            successMessage = "Backup exported successfully!"
                            showSuccessDialog = true
                        },
                        onFailure = { error ->
                            errorMessage = "Export failed: ${error.message}"
                            showErrorDialog = true
                        }
                    )
                } finally {
                    isExporting = false
                }
            }
        }
    }

    // Import backup launcher
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            pendingRestoreUri = it
            showConfirmRestoreDialog = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Data Management Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_receipt_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Data Management",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Export Data Button
                Button(
                    onClick = {
                        exportLauncher.launch(backupUtils.getBackupFileName())
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isExporting && !isImporting
                ) {
                    if (isExporting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_arrow_upload_ready_24),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(if (isExporting) "Exporting..." else "Export Data")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Import Data Button
                OutlinedButton(
                    onClick = {
                        importLauncher.launch(arrayOf("application/json"))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isExporting && !isImporting
                ) {
                    if (isImporting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_download_for_offline_24),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(if (isImporting) "Importing..." else "Import Data")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "• Export creates a backup file of all your receipts and cab information\n" +
                          "• Import will replace all existing data with the backup file data\n" +
                          "• Use this to transfer data to a new device or create backups",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }
    }

    // Confirm Restore Dialog
    if (showConfirmRestoreDialog) {
        AlertDialog(
            onDismissRequest = {
                showConfirmRestoreDialog = false
                pendingRestoreUri = null
            },
            title = { Text("Confirm Data Import") },
            text = {
                Text(
                    "This will replace ALL existing receipts and cab information with the data from the backup file. This action cannot be undone.\n\nAre you sure you want to continue?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingRestoreUri?.let { uri ->
                            scope.launch {
                                isImporting = true
                                try {
                                    val importResult = backupUtils.importBackupFromUri(uri)
                                    importResult.fold(
                                        onSuccess = { backupData ->
                                            val restoreResult = backupUtils.restoreFromBackup(backupData)
                                            restoreResult.fold(
                                                onSuccess = {
                                                    successMessage = "Data imported successfully! ${backupData.receipts.size} receipts restored."
                                                    showSuccessDialog = true
                                                },
                                                onFailure = { error ->
                                                    errorMessage = "Restore failed: ${error.message}"
                                                    showErrorDialog = true
                                                }
                                            )
                                        },
                                        onFailure = { error ->
                                            errorMessage = "Import failed: ${error.message}"
                                            showErrorDialog = true
                                        }
                                    )
                                } finally {
                                    isImporting = false
                                }
                            }
                        }
                        showConfirmRestoreDialog = false
                        pendingRestoreUri = null
                    }
                ) {
                    Text("Import", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showConfirmRestoreDialog = false
                        pendingRestoreUri = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Success") },
            text = { Text(successMessage) },
            confirmButton = {
                TextButton(onClick = { showSuccessDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    // Error Dialog
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}
