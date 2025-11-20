package com.fueians.medicationapp.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.UUID

@Entity(tableName = "refills")
data class RefillEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "medication_id")
    val medicationId: String,

    @ColumnInfo(name = "total_quantity")
    val totalQuantity: Int,

    @ColumnInfo(name = "remaining_quantity")
    val remainingQuantity: Int,

    @ColumnInfo(name = "last_refill_date")
    val lastRefillDate: Long, // Timestamp

    @ColumnInfo(name = "next_refill_date")
    val nextRefillDate: Long, // Timestamp

    @ColumnInfo(name = "prescription_number")
    val prescriptionNumber: String? = null,

    @ColumnInfo(name = "pharmacy_name")
    val pharmacyName: String? = null,

    @ColumnInfo(name = "reminder_enabled")
    val reminderEnabled: Boolean = true,

    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Instant.now(),
)