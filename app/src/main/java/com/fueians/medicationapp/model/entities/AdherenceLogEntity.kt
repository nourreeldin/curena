package com.fueians.medicationapp.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * AdherenceStatus Enum
 *
 * Represents the status of a medication dose
 */
enum class AdherenceStatus {
    TAKEN,      // Dose was taken as scheduled
    MISSED,     // Dose was not taken and time has passed
    SKIPPED,    // User intentionally skipped the dose
    PENDING     // Scheduled but not yet time to take
}

/**
 * AdherenceLogEntity
 *
 * Responsibility: Represent adherence tracking data in the database.
 *
 * This entity logs medication adherence events, tracking when doses were
 * taken, missed, or skipped. It provides historical data for adherence
 * analysis, reports, and patient monitoring.
 *
 * Related Classes: AdherenceLogDao, ScheduleRepository
 */
@Entity(
    tableName = "adherence_logs",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MedicationEntity::class,
            parentColumns = ["id"],
            childColumns = ["medication_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MedicationScheduleEntity::class,
            parentColumns = ["id"],
            childColumns = ["schedule_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["medication_id"]),
        Index(value = ["schedule_id"]),
        Index(value = ["status"]),
        Index(value = ["scheduled_time"]),
        Index(value = ["timestamp"]),
        Index(value = ["user_id", "medication_id"]),
        Index(value = ["user_id", "timestamp"])
    ]
)
data class AdherenceLogEntity(
    /**
     * Log ID - Primary key
     * Unique identifier for this adherence log entry
     */
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    /**
     * User ID - Foreign key
     * References the user who owns this adherence log
     */
    @ColumnInfo(name = "user_id")
    val userId: String,

    /**
     * Medication ID - Foreign key
     * References the medication this log is tracking
     */
    @ColumnInfo(name = "medication_id")
    val medicationId: String,

    /**
     * Schedule ID - Foreign key
     * References the specific schedule entry this log is for
     */
    @ColumnInfo(name = "schedule_id")
    val scheduleId: String,

    /**
     * Scheduled time
     * Unix timestamp (milliseconds) when the dose was scheduled to be taken
     */
    @ColumnInfo(name = "scheduled_time")
    val scheduledTime: Long,

    /**
     * Actual taken time (optional)
     * Unix timestamp (milliseconds) when the dose was actually taken
     * Null if status is MISSED or SKIPPED
     */
    @ColumnInfo(name = "taken_time")
    val takenTime: Long? = null,

    /**
     * Adherence status
     * Current status: TAKEN, MISSED, SKIPPED, or PENDING
     */
    @ColumnInfo(name = "status")
    val status: AdherenceStatus,

    /**
     * Optional notes
     * User or caregiver notes about this dose
     * (e.g., "felt nauseous", "forgot", "took with food")
     */
    @ColumnInfo(name = "notes")
    val notes: String? = null,

    /**
     * Log timestamp
     * Unix timestamp (milliseconds) when this log entry was created
     */
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Relationships (defined in class diagram):
     * - 1 AdherenceLogEntity belongs to 1 UserEntity
     * - 1 AdherenceLogEntity tracks 1 MedicationEntity
     * - 1 AdherenceLogEntity relates to 1 MedicationScheduleEntity
     * - 0..* AdherenceLogEntity included in 1 ReportEntity
     *
     * Managed by:
     * - AdherenceLogDao: Database operations (CRUD)
     * - ScheduleRepository: Logging adherence events
     * - MedicationRepository: Tracking medication adherence
     * - ReportRepository: Generating adherence reports
     * - Synced with Supabase via SupabaseClient
     */

    /**
     * Helper function to check if dose was taken on time
     * Returns true if taken within acceptable window (default ±30 minutes)
     */
    fun isTakenOnTime(windowMinutes: Int = 30): Boolean {
        if (status != AdherenceStatus.TAKEN || takenTime == null) return false
        val windowMillis = windowMinutes * 60 * 1000L
        val difference = Math.abs(takenTime - scheduledTime)
        return difference <= windowMillis
    }

    /**
     * Helper function to calculate delay in taking medication
     * Returns delay in minutes (positive = late, negative = early, null if not taken)
     */
    fun getDelayInMinutes(): Long? {
        if (takenTime == null) return null
        return (takenTime - scheduledTime) / (60 * 1000L)
    }

    /**
     * Helper function to check if this represents adherence
     * Returns true for TAKEN status
     */
    fun isAdherent(): Boolean {
        return status == AdherenceStatus.TAKEN
    }

    /**
     * Helper function to check if this represents non-adherence
     * Returns true for MISSED or SKIPPED status
     */
    fun isNonAdherent(): Boolean {
        return status == AdherenceStatus.MISSED || status == AdherenceStatus.SKIPPED
    }

    /**
     * Helper function to get adherence score
     * Returns 1.0 for taken on time, 0.5 for taken late, 0.0 for missed/skipped
     */
    fun getAdherenceScore(onTimeWindowMinutes: Int = 30): Double {
        return when (status) {
            AdherenceStatus.TAKEN -> if (isTakenOnTime(onTimeWindowMinutes)) 1.0 else 0.5
            AdherenceStatus.MISSED, AdherenceStatus.SKIPPED -> 0.0
            AdherenceStatus.PENDING -> 0.0
        }
    }

    /**
     * Helper function to get human-readable status description
     */
    fun getStatusDescription(): String {
        return when (status) {
            AdherenceStatus.TAKEN -> {
                val delay = getDelayInMinutes()
                when {
                    delay == null -> "Taken"
                    delay > 30 -> "Taken (${delay}min late)"
                    delay < -30 -> "Taken (${-delay}min early)"
                    else -> "Taken on time"
                }
            }
            AdherenceStatus.MISSED -> "Missed"
            AdherenceStatus.SKIPPED -> "Skipped"
            AdherenceStatus.PENDING -> "Pending"
        }
    }
}
