package dev.thalha.cabslip.data.model

import dev.thalha.cabslip.data.entity.CabInfo
import dev.thalha.cabslip.data.entity.Receipt
import kotlinx.serialization.Serializable

@Serializable
data class BackupData(
    val version: Int = 1,
    val timestamp: Long,
    val cabInfo: CabInfo?,
    val receipts: List<Receipt>
)
