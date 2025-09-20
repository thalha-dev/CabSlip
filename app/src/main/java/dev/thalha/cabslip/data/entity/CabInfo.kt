package dev.thalha.cabslip.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cab_info")
data class CabInfo(
    @PrimaryKey
    val id: Int = 1, // Always 1 for single row
    val cabName: String,
    val cabAddress: String,
    val primaryContact: String,
    val secondaryContact: String? = null,
    val email: String,
    val logoPath: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)
