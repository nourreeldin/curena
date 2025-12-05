package com.fueians.medicationapp.model.entities

import androidx.room.*
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable(with = InstantSerializer::class)
@Entity(
    tableName = "notification_settings",
    indices = [
        Index(value = ["user_id"], unique = true)
    ]
)
data class NotificationSettingsEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "medication_reminders_enabled")
    val medicationRemindersEnabled: Boolean = true,

    @ColumnInfo(name = "refill_reminders_enabled")
    val refillRemindersEnabled: Boolean = true,

    @ColumnInfo(name = "appointment_reminders_enabled")
    val appointmentRemindersEnabled: Boolean = true,

    @ColumnInfo(name = "missed_dose_alerts_enabled")
    val missedDoseAlertsEnabled: Boolean = true,

    @ColumnInfo(name = "notification_sound")
    val notificationSound: String? = null,

    @ColumnInfo(name = "vibration_enabled")
    val vibrationEnabled: Boolean = true,

    @ColumnInfo(name = "quiet_hours_enabled")
    val quietHoursEnabled: Boolean = false,

    @ColumnInfo(name = "quiet_hours_start")
    val quietHoursStart: String? = null, // "HH:mm:ss" format

    @ColumnInfo(name = "quiet_hours_end")
    val quietHoursEnd: String? = null,   // "HH:mm:ss" format

    @ColumnInfo(name = "critical_override")
    val criticalOverride: Boolean = true,

    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Instant.now(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Instant = Instant.now()
)
