package dev.thalha.cabslip.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "receipts")
data class Receipt(
    @PrimaryKey
    val receiptId: String,
    val boardingLocation: String,
    val destination: String,
    val tripStartDate: Long, // Timestamp in milliseconds
    val tripEndDate: Long?, // Nullable timestamp
    val pricePerKm: Double,
    val waitingChargePerHr: Double,
    val waitingHrs: Double,
    val totalKm: Double,
    val tollParking: Double,
    val bata: Double,
    val driverName: String,
    val driverMobile: String,
    val vehicleNumber: String,
    val ownerSignaturePath: String?,
    val baseFare: Double, // Calculated: pricePerKm * totalKm
    val waitingFee: Double, // Calculated: waitingChargePerHr * waitingHrs
    val totalFee: Double, // Calculated: baseFare + tollParking + bata + waitingFee
    val createdAt: Long,
    val updatedAt: Long
)
