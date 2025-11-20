package com.fueians.medicationapp.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.UUID

// Assuming AdherenceStatus is a simple enum or string defined elsewhere (taken, missed, skipped)
typealias AdherenceStatus = String

@Entity(tableName = "adherence_logs")
data class AdherenceLogEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "medication_id")
    val medicationId: String,

    @ColumnInfo(name = "schedule_id")
    val scheduleId: String,

    @ColumnInfo(name = "scheduled_time")
    val scheduledTime: Long, // Timestamp

    @ColumnInfo(name = "taken_time")
    val takenTime: Long? = null, // Actual taken time

    val status: AdherenceStatus, // e.g., "taken", "missed"

    val notes: String? = null,

    val timestamp: Long = Instant.now().toEpochMilli(), // Log creation timestamp
)