package com.fueians.medicationapp.model.entities
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index
import kotlinx.serialization.Serializable

/**
 * MedicationScheduleEntity
 *
 * Responsibility: Represent medication schedule data in the database.
 *
 * This entity stores individual scheduled doses for medications, including
 * timing, adherence tracking, and notification settings. Each schedule
 * represents a specific time when a medication should be taken.
 *
 * Related Classes: MedicationScheduleDao, ScheduleRepository
 */
@Serializable
@Entity(
    tableName = "medication_schedules",
    foreignKeys = [
        ForeignKey(
            entity = MedicationEntity::class,
            parentColumns = ["id"],
            childColumns = ["medication_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["medication_id"]),
        Index(value = ["scheduled_time"]),
        Index(value = ["is_taken"]),
        Index(value = ["is_missed"]),
        Index(value = ["notification_enabled"])
    ]
)
data class MedicationScheduleEntity(
    /**
     * Schedule ID - Primary key
     * Unique identifier for this scheduled dose
     */
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    /**
     * Associated medication ID - Foreign key
     * References the medication this schedule belongs to
     */
    @ColumnInfo(name = "medication_id")
    val medicationId: String,

    /**
     * Scheduled time timestamp
     * Unix timestamp (milliseconds) when the dose should be taken
     */
    @ColumnInfo(name = "scheduled_time")
    val scheduledTime: Long,

    /**
     * Dosage for this schedule
     * Specific dosage amount for this scheduled dose
     * (e.g., "500mg", "2 tablets", "10ml")
     */
    @ColumnInfo(name = "dosage_amount")
    val dosageAmount: String,

    /**
     * Whether dose was taken
     * True if the user has confirmed taking this dose
     */
    @ColumnInfo(name = "is_taken")
    val isTaken: Boolean = false,

    /**
     * When dose was taken (optional)
     * Unix timestamp (milliseconds) of actual dose intake
     * Null if not yet taken
     */
    @ColumnInfo(name = "taken_at")
    val takenAt: Long? = null,

    /**
     * Whether dose was missed
     * True if the scheduled time has passed without being taken
     */
    @ColumnInfo(name = "is_missed")
    val isMissed: Boolean = false,

    /**
     * Notification status
     * Whether notifications are enabled for this scheduled dose
     */
    @ColumnInfo(name = "notification_enabled")
    val notificationEnabled: Boolean = true,

    /**
     * Reminder time in minutes
     * How many minutes before scheduled time to send reminder
     * (e.g., 15 = remind 15 minutes before, 0 = remind at scheduled time)
     */
    @ColumnInfo(name = "reminder_minutes_before")
    val reminderMinutesBefore: Int = 15,

    /**
     * Creation timestamp
     * Unix timestamp (milliseconds) when the schedule was created
     */
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Relationships (defined in class diagram):
     * - 1 MedicationScheduleEntity belongs to 1 MedicationEntity
     *
     * Managed by:
     * - MedicationScheduleDao: Database operations (CRUD)
     * - ScheduleRepository: Business logic and scheduling management
     * - ReminderService: Handles reminder notifications
     * - NotificationService: Sends notifications to user
     * - Synced with Supabase via SupabaseClient
     */

    /**
     * Helper function to check if dose is upcoming
     */
    fun isUpcoming(): Boolean {
        val now = System.currentTimeMillis()
        return !isTaken && !isMissed && scheduledTime > now
    }

    /**
     * Helper function to check if dose is overdue
     */
    fun isOverdue(): Boolean {
        val now = System.currentTimeMillis()
        return !isTaken && !isMissed && scheduledTime < now
    }

    /**
     * Helper function to get reminder time timestamp
     */
    fun getReminderTime(): Long {
        return scheduledTime - (reminderMinutesBefore * 60 * 1000L)
    }

    /**
     * Helper function to check if reminder should be sent now
     */
    fun shouldSendReminder(): Boolean {
        val now = System.currentTimeMillis()
        val reminderTime = getReminderTime()
        return notificationEnabled && !isTaken && !isMissed &&
                now >= reminderTime && now < scheduledTime
    }

    /**
     * Helper function to calculate time until scheduled dose
     */
    fun getTimeUntilScheduled(): Long {
        return scheduledTime - System.currentTimeMillis()
    }

    /**
     * Helper function to calculate adherence window status
     * Returns true if within acceptable window (e.g., ±30 minutes)
     */
    fun isWithinAdherenceWindow(windowMinutes: Int = 30): Boolean {
        if (takenAt == null) return false
        val windowMillis = windowMinutes * 60 * 1000L
        val difference = Math.abs(takenAt - scheduledTime)
        return difference <= windowMillis
    }
}
