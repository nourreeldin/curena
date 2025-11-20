package com.fueians.medicationapp.model.entities
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * MedicationEntity
 *
 * Responsibility: Represent medication data in the database.
 *
 * This entity stores comprehensive medication information including dosage,
 * frequency, scheduling details, and status. Each medication is owned by a
 * specific user and can have multiple schedules, adherence logs, and refills.
 *
 * Related Classes: MedicationDao, MedicationRepository
 */
@Entity(
    tableName = "medications",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["is_active"]),
        Index(value = ["start_date"]),
        Index(value = ["end_date"])
    ]
)
data class MedicationEntity(
    /**
     * Medication ID - Primary key
     * Unique identifier for the medication
     */
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    /**
     * Owner user ID - Foreign key
     * References the user who owns this medication
     */
    @ColumnInfo(name = "user_id")
    val userId: String,

    /**
     * Medication name
     * Name of the medication (e.g., "Lisinopril", "Aspirin")
     */
    @ColumnInfo(name = "name")
    val name: String,

    /**
     * Dosage amount
     * Dosage specification (e.g., "500mg", "10ml", "2 tablets")
     */
    @ColumnInfo(name = "dosage")
    val dosage: String,

    /**
     * Frequency
     * How often the medication should be taken
     * (e.g., "twice daily", "every 8 hours", "as needed")
     */
    @ColumnInfo(name = "frequency")
    val frequency: String,

    /**
     * Special instructions (optional)
     * Additional instructions for taking the medication
     * (e.g., "take with food", "avoid alcohol", "take on empty stomach")
     */
    @ColumnInfo(name = "instructions")
    val instructions: String? = null,

    /**
     * Start date timestamp
     * Unix timestamp (milliseconds) when medication treatment begins
     */
    @ColumnInfo(name = "start_date")
    val startDate: Long,

    /**
     * End date timestamp (optional)
     * Unix timestamp (milliseconds) when medication treatment ends
     * Null indicates ongoing/indefinite treatment
     */
    @ColumnInfo(name = "end_date")
    val endDate: Long? = null,

    /**
     * Active status
     * Indicates whether the medication is currently active
     * False means discontinued or completed
     */
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    /**
     * Creation timestamp
     * Unix timestamp (milliseconds) when the record was created
     */
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    /**
     * Last update timestamp
     * Unix timestamp (milliseconds) of the last record update
     */
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Relationships (defined in class diagram):
     * - 1 MedicationEntity belongs to 1 UserEntity (owner)
     * - 1 MedicationEntity has 0..* MedicationScheduleEntity
     * - 1 MedicationEntity is tracked by 0..* AdherenceLogEntity
     * - 1 MedicationEntity has 0..1 RefillEntity
     * - 0..* MedicationEntity involved in 0..* DrugInteractionEntity
     *
     * Managed by:
     * - MedicationDao: Database operations (CRUD)
     * - MedicationRepository: Business logic and data management
     * - Synced with Supabase via SupabaseClient
     * - Notifications handled by NotificationService and ReminderService
     */

    /**
     * Helper function to check if medication is currently ongoing
     */
    fun isOngoing(): Boolean {
        val now = System.currentTimeMillis()
        return isActive && startDate <= now && (endDate == null || endDate > now)
    }

    /**
     * Helper function to check if medication treatment has ended
     */
    fun hasEnded(): Boolean {
        return endDate != null && endDate < System.currentTimeMillis()
    }
}