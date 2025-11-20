package com.fueians.medicationapp.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.UUID

@Entity(tableName = "medication_schedules")
data class MedicationScheduleEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "medication_id")
    val medicationId: String,

    @ColumnInfo(name = "scheduled_time")
    val scheduledTime: Long, // Timestamp

    @ColumnInfo(name = "dosage_amount")
    val dosageAmount: String,

    @ColumnInfo(name = "is_taken")
    val isTaken: Boolean = false,

    @ColumnInfo(name = "taken_at")
    val takenAt: Long? = null, // Timestamp when dose was actually taken

    @ColumnInfo(name = "is_missed")
    val isMissed: Boolean = false,

    @ColumnInfo(name = "notification_enabled")
    val notificationEnabled: Boolean = true,

    @ColumnInfo(name = "reminder_minutes_before")
    val reminderMinutesBefore: Int = 0,

    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Instant.now(),
)