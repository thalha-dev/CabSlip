package dev.thalha.cabslip.utils

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import dev.thalha.cabslip.data.model.BackupData
import dev.thalha.cabslip.data.repository.CabSlipRepository
import java.io.IOException

class BackupRestoreUtils(
    private val context: Context,
    private val repository: CabSlipRepository
) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    suspend fun createBackup(): BackupData = withContext(Dispatchers.IO) {
        val cabInfo = repository.getCabInfoSync()
        val receipts = repository.getAllReceiptsSync()

        android.util.Log.d("BackupRestore", "Backup created with ${receipts.size} receipts (no signatures needed - stored in cab info)")

        BackupData(
            timestamp = System.currentTimeMillis(),
            cabInfo = cabInfo,
            receipts = receipts
        )
    }

    suspend fun exportBackupToUri(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val backupData = createBackup()
            val jsonString = json.encodeToString(backupData)

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(jsonString.toByteArray())
            } ?: return@withContext Result.failure(IOException("Could not open output stream"))

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importBackupFromUri(uri: Uri): Result<BackupData> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes().decodeToString()
            } ?: return@withContext Result.failure(IOException("Could not read file"))

            val backupData = json.decodeFromString<BackupData>(jsonString)
            Result.success(backupData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restoreFromBackup(backupData: BackupData): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Clear existing data
            repository.deleteAllReceipts()

            // Restore cab info if exists (includes owner signature)
            backupData.cabInfo?.let { cabInfo ->
                repository.insertOrUpdateCabInfo(cabInfo)
                android.util.Log.d("BackupRestore", "Restored cab info with signature")
            }

            // Restore receipts
            backupData.receipts.forEach { receipt ->
                repository.insertReceipt(receipt)
            }
            android.util.Log.d("BackupRestore", "Restored ${backupData.receipts.size} receipts")

            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("BackupRestore", "Failed to restore backup", e)
            Result.failure(e)
        }
    }

    fun getBackupFileName(): String {
        val timestamp = java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", java.util.Locale.getDefault())
            .format(java.util.Date())
        return "cabslip_backup_$timestamp.json"
    }
}
