package com.fueians.medicationapp.model.entities
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index
import kotlinx.serialization.Serializable

/**
 * RefillEntity
 *
 * Responsibility: Represent medication refill tracking data.
 *
 * This entity tracks medication refills, monitoring quantities, refill dates,
 * and pharmacy information. It helps patients manage their medication supply
 * and sends reminders when refills are needed to prevent running out of
 * essential medications.
 *
 * Related Classes: RefillDao, MedicationRepository
 */
@Serializable
@Entity(
    tableName = "refills",
    foreignKeys = [
        ForeignKey(
            entity = MedicationEntity::class,
            parentColumns = ["id"],
            childColumns = ["medication_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["medication_id"], unique = true),
        Index(value = ["next_refill_date"]),
        Index(value = ["reminder_enabled"]),
        Index(value = ["remaining_quantity"])
    ]
)
data class RefillEntity(
    /**
     * Refill ID - Primary key
     * Unique identifier for this refill record
     */
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    /**
     * Associated medication ID - Foreign key
     * References the medication this refill tracks
     * Note: One medication has at most one refill record (0..1 relationship)
     */
    @ColumnInfo(name = "medication_id")
    val medicationId: String,

    /**
     * Total quantity prescribed
     * Total number of doses/units in the prescription
     * (e.g., 90 tablets, 300ml)
     */
    @ColumnInfo(name = "total_quantity")
    val totalQuantity: Int,

    /**
     * Remaining quantity
     * Current number of doses/units remaining
     */
    @ColumnInfo(name = "remaining_quantity")
    val remainingQuantity: Int,

    /**
     * Last refill date timestamp
     * Unix timestamp (milliseconds) of the most recent refill
     */
    @ColumnInfo(name = "last_refill_date")
    val lastRefillDate: Long,

    /**
     * Next scheduled refill date timestamp
     * Unix timestamp (milliseconds) when next refill is due
     */
    @ColumnInfo(name = "next_refill_date")
    val nextRefillDate: Long,

    /**
     * Prescription number (optional)
     * Pharmacy prescription reference number
     */
    @ColumnInfo(name = "prescription_number")
    val prescriptionNumber: String? = null,

    /**
     * Pharmacy name (optional)
     * Name of the pharmacy where medication is filled
     */
    @ColumnInfo(name = "pharmacy_name")
    val pharmacyName: String? = null,

    /**
     * Refill reminder status
     * Whether reminders are enabled for this refill
     */
    @ColumnInfo(name = "reminder_enabled")
    val reminderEnabled: Boolean = true,

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
     * - 1 RefillEntity belongs to 1 MedicationEntity
     * - 1 MedicationEntity has 0..1 RefillEntity
     *
     * Managed by:
     * - RefillDao: Database operations (CRUD)
     * - MedicationRepository: Refill tracking and updates
     * - ReminderService: Sends refill reminder notifications
     * - Synced with Supabase via SupabaseClient
     */

    companion object {
        // Default days before refill to send reminder
        const val DEFAULT_REMINDER_DAYS_BEFORE = 7

        // Low quantity threshold percentage
        const val LOW_QUANTITY_THRESHOLD = 0.25 // 25%

        // Critical quantity threshold percentage
        const val CRITICAL_QUANTITY_THRESHOLD = 0.10 // 10%
    }

    /**
     * Helper function to calculate quantity percentage remaining
     */
    fun getQuantityPercentage(): Double {
        if (totalQuantity <= 0) return 0.0
        return remainingQuantity.toDouble() / totalQuantity.toDouble()
    }

    /**
     * Helper function to check if quantity is low
     */
    fun isQuantityLow(): Boolean {
        return getQuantityPercentage() <= LOW_QUANTITY_THRESHOLD
    }

    /**
     * Helper function to check if quantity is critical
     */
    fun isQuantityCritical(): Boolean {
        return getQuantityPercentage() <= CRITICAL_QUANTITY_THRESHOLD
    }

    /**
     * Helper function to check if refill is due soon
     * Default: within 7 days
     */
    fun isRefillDueSoon(daysThreshold: Int = DEFAULT_REMINDER_DAYS_BEFORE): Boolean {
        val now = System.currentTimeMillis()
        val daysUntilRefill = (nextRefillDate - now) / (24 * 60 * 60 * 1000L)
        return daysUntilRefill in 0..daysThreshold
    }

    /**
     * Helper function to check if refill is overdue
     */
    fun isRefillOverdue(): Boolean {
        val now = System.currentTimeMillis()
        return nextRefillDate < now
    }

    /**
     * Helper function to check if refill needs immediate attention
     */
    fun needsImmediateAttention(): Boolean {
        return isRefillOverdue() || isQuantityCritical()
    }

    /**
     * Helper function to get days until refill
     */
    fun getDaysUntilRefill(): Long {
        val now = System.currentTimeMillis()
        return (nextRefillDate - now) / (24 * 60 * 60 * 1000L)
    }

    /**
     * Helper function to get days since last refill
     */
    fun getDaysSinceLastRefill(): Long {
        val now = System.currentTimeMillis()
        return (now - lastRefillDate) / (24 * 60 * 60 * 1000L)
    }

    /**
     * Helper function to check if reminder should be sent
     */
    fun shouldSendReminder(daysThreshold: Int = DEFAULT_REMINDER_DAYS_BEFORE): Boolean {
        return reminderEnabled && (isRefillDueSoon(daysThreshold) || isQuantityLow())
    }

    /**
     * Helper function to get refill status
     */
    fun getRefillStatus(): RefillStatus {
        return when {
            isRefillOverdue() -> RefillStatus.OVERDUE
            isQuantityCritical() -> RefillStatus.CRITICAL
            isRefillDueSoon() -> RefillStatus.DUE_SOON
            isQuantityLow() -> RefillStatus.LOW_QUANTITY
            else -> RefillStatus.ADEQUATE
        }
    }

    /**
     * Helper function to get status color for UI
     */
    fun getStatusColor(): String {
        return when (getRefillStatus()) {
            RefillStatus.OVERDUE -> "#C62828"      // Dark Red
            RefillStatus.CRITICAL -> "#EF5350"     // Red
            RefillStatus.DUE_SOON -> "#FF7043"     // Deep Orange
            RefillStatus.LOW_QUANTITY -> "#FFA726" // Orange
            RefillStatus.ADEQUATE -> "#66BB6A"     // Green
        }
    }

    /**
     * Helper function to get status message
     */
    fun getStatusMessage(): String {
        return when (getRefillStatus()) {
            RefillStatus.OVERDUE -> "Refill overdue - contact pharmacy"
            RefillStatus.CRITICAL -> "Critical: Only $remainingQuantity remaining"
            RefillStatus.DUE_SOON -> {
                val days = getDaysUntilRefill()
                "Refill due in $days day${if (days != 1L) "s" else ""}"
            }
            RefillStatus.LOW_QUANTITY -> "Low quantity: $remainingQuantity remaining"
            RefillStatus.ADEQUATE -> "$remainingQuantity doses remaining"
        }
    }

    /**
     * Helper function to calculate estimated days supply remaining
     * Requires daily dosage frequency
     */
    fun getEstimatedDaysSupply(dosesPerDay: Int): Int {
        if (dosesPerDay <= 0) return 0
        return remainingQuantity / dosesPerDay
    }

    /**
     * Helper function to get refill urgency score
     * Higher score = more urgent
     */
    fun getUrgencyScore(): Int {
        return when (getRefillStatus()) {
            RefillStatus.OVERDUE -> 5
            RefillStatus.CRITICAL -> 4
            RefillStatus.DUE_SOON -> 3
            RefillStatus.LOW_QUANTITY -> 2
            RefillStatus.ADEQUATE -> 1
        }
    }
}

/**
 * Refill status enum
 */
enum class RefillStatus {
    OVERDUE,        // Past refill date
    CRITICAL,       // Very low quantity (≤10%)
    DUE_SOON,       // Refill needed within threshold
    LOW_QUANTITY,   // Low quantity (≤25%)
    ADEQUATE        // Sufficient supply
}