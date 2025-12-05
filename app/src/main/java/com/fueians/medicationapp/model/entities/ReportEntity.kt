package com.fueians.medicationapp.model.entities
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index
import kotlinx.serialization.Serializable

/**
 * Report types available in the system
 */
enum class ReportType {
    ADHERENCE,          // Medication adherence report
    SUMMARY,            // Overall medication summary
    DETAILED,           // Detailed medication breakdown
    CAREGIVER,          // Caregiver monitoring report
    MONTHLY,            // Monthly progress report
    CUSTOM              // Custom date range report
}

/**
 * ReportEntity
 *
 * Responsibility: Represent generated report data.
 *
 * This entity stores generated medication reports including adherence statistics,
 * medication summaries, and detailed analytics. Reports can be viewed in-app or
 * exported to share with healthcare providers or caregivers.
 *
 * Related Classes: ReportDao, ReportRepository
 */
@Serializable
@Entity(
    tableName = "reports",
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
        Index(value = ["report_type"]),
        Index(value = ["created_at"]),
        Index(value = ["start_date"]),
        Index(value = ["end_date"]),
        Index(value = ["adherence_rate"])
    ]
)
data class ReportEntity(
    /**
     * Report ID - Primary key
     * Unique identifier for this report
     */
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    /**
     * User ID - Foreign key
     * References the user this report belongs to
     */
    @ColumnInfo(name = "user_id")
    val userId: String,

    /**
     * Type of report
     * Report category: ADHERENCE, SUMMARY, DETAILED, CAREGIVER, MONTHLY, CUSTOM
     */
    @ColumnInfo(name = "report_type")
    val reportType: String,

    /**
     * Report title
     * Display title for the report
     * (e.g., "Monthly Adherence Report - October 2024")
     */
    @ColumnInfo(name = "title")
    val title: String,

    /**
     * Report period start date
     * Unix timestamp (milliseconds) of the report start date
     */
    @ColumnInfo(name = "start_date")
    val startDate: Long,

    /**
     * Report period end date
     * Unix timestamp (milliseconds) of the report end date
     */
    @ColumnInfo(name = "end_date")
    val endDate: Long,

    /**
     * Overall adherence rate
     * Percentage of doses taken on time (0.0 to 100.0)
     */
    @ColumnInfo(name = "adherence_rate")
    val adherenceRate: Float,

    /**
     * Number of medications tracked
     * Total count of medications included in this report
     */
    @ColumnInfo(name = "total_medications")
    val totalMedications: Int,

    /**
     * Total doses scheduled
     * Total number of scheduled doses in the report period
     */
    @ColumnInfo(name = "total_doses")
    val totalDoses: Int,

    /**
     * Doses taken
     * Number of doses that were taken
     */
    @ColumnInfo(name = "taken_doses")
    val takenDoses: Int,

    /**
     * Doses missed
     * Number of doses that were missed or skipped
     */
    @ColumnInfo(name = "missed_doses")
    val missedDoses: Int,

    /**
     * JSON data for report
     * Detailed report data stored as JSON string
     * Contains medication-specific data, charts data, etc.
     */
    @ColumnInfo(name = "report_data")
    val reportData: String,

    /**
     * Exported file path (optional)
     * File system path to exported PDF/CSV file
     */
    @ColumnInfo(name = "file_path")
    val filePath: String? = null,

    /**
     * Creation timestamp
     * Unix timestamp (milliseconds) when the report was generated
     */
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Relationships (defined in class diagram):
     * - 1 ReportEntity belongs to 1 UserEntity
     * - 1 ReportEntity includes 0..* AdherenceLogEntity data
     *
     * Managed by:
     * - ReportDao: Database operations (CRUD)
     * - ReportRepository: Report generation and storage
     * - ReportGenerationService: Creates and formats reports
     * - Synced with Supabase via SupabaseClient
     */

    companion object {
        // Adherence rate thresholds
        const val EXCELLENT_ADHERENCE = 95.0f
        const val GOOD_ADHERENCE = 80.0f
        const val FAIR_ADHERENCE = 60.0f
    }

    /**
     * Helper function to get report type as enum
     */
    fun getReportTypeEnum(): ReportType {
        return try {
            ReportType.valueOf(reportType.uppercase())
        } catch (e: IllegalArgumentException) {
            ReportType.SUMMARY
        }
    }

    /**
     * Helper function to get adherence category
     */
    fun getAdherenceCategory(): AdherenceCategory {
        return when {
            adherenceRate >= EXCELLENT_ADHERENCE -> AdherenceCategory.EXCELLENT
            adherenceRate >= GOOD_ADHERENCE -> AdherenceCategory.GOOD
            adherenceRate >= FAIR_ADHERENCE -> AdherenceCategory.FAIR
            else -> AdherenceCategory.POOR
        }
    }

    /**
     * Helper function to check if adherence is concerning
     */
    fun isAdherenceConcerning(): Boolean {
        return adherenceRate < GOOD_ADHERENCE
    }

    /**
     * Helper function to get report duration in days
     */
    fun getReportDurationDays(): Long {
        return (endDate - startDate) / (24 * 60 * 60 * 1000L)
    }

    /**
     * Helper function to calculate actual adherence rate
     * (in case stored rate needs recalculation)
     */
    fun calculateAdherenceRate(): Float {
        if (totalDoses == 0) return 0.0f
        return (takenDoses.toFloat() / totalDoses.toFloat()) * 100
    }

    /**
     * Helper function to get skipped doses
     */
    fun getSkippedDoses(): Int {
        return totalDoses - takenDoses - missedDoses
    }

    /**
     * Helper function to check if report has been exported
     */
    fun isExported(): Boolean {
        return filePath != null
    }

    /**
     * Helper function to get adherence color for UI
     */
    fun getAdherenceColor(): String {
        return when (getAdherenceCategory()) {
            AdherenceCategory.EXCELLENT -> "#66BB6A"  // Green
            AdherenceCategory.GOOD -> "#9CCC65"       // Light Green
            AdherenceCategory.FAIR -> "#FFA726"       // Orange
            AdherenceCategory.POOR -> "#EF5350"       // Red
        }
    }

    /**
     * Helper function to get adherence description
     */
    fun getAdherenceDescription(): String {
        return when (getAdherenceCategory()) {
            AdherenceCategory.EXCELLENT -> "Excellent adherence"
            AdherenceCategory.GOOD -> "Good adherence"
            AdherenceCategory.FAIR -> "Fair adherence - room for improvement"
            AdherenceCategory.POOR -> "Poor adherence - needs attention"
        }
    }

    /**
     * Helper function to get formatted date range
     */
    fun getDateRangeFormatted(): String {
        val startDateStr = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
            .format(java.util.Date(startDate))
        val endDateStr = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
            .format(java.util.Date(endDate))
        return "$startDateStr - $endDateStr"
    }

    /**
     * Helper function to get report summary
     */
    fun getSummary(): String {
        return """
            Report Period: ${getDateRangeFormatted()}
            Medications Tracked: $totalMedications
            Adherence Rate: ${String.format("%.1f", adherenceRate)}%
            Doses Taken: $takenDoses / $totalDoses
            Missed: $missedDoses
        """.trimIndent()
    }

    /**
     * Helper function to get adherence percentage as integer
     */
    fun getAdherencePercentage(): Int {
        return adherenceRate.toInt()
    }

    /**
     * Helper function to calculate missed dose percentage
     */
    fun getMissedDosePercentage(): Float {
        if (totalDoses == 0) return 0.0f
        return (missedDoses.toFloat() / totalDoses.toFloat()) * 100
    }

    /**
     * Helper function to get report age in days
     */
    fun getReportAgeInDays(): Long {
        val now = System.currentTimeMillis()
        return (now - createdAt) / (24 * 60 * 60 * 1000L)
    }

    /**
     * Helper function to check if report is recent
     * Default: within 7 days
     */
    fun isRecent(daysThreshold: Int = 7): Boolean {
        return getReportAgeInDays() <= daysThreshold
    }

    /**
     * Helper function to get average doses per day
     */
    fun getAverageDosesPerDay(): Float {
        val days = getReportDurationDays()
        if (days == 0L) return 0.0f
        return totalDoses.toFloat() / days.toFloat()
    }

    /**
     * Helper function to get adherence trend indicator
     * Returns positive/negative/neutral based on adherence level
     */
    fun getAdherenceTrend(): String {
        return when {
            adherenceRate >= EXCELLENT_ADHERENCE -> "↑ Excellent"
            adherenceRate >= GOOD_ADHERENCE -> "→ Stable"
            else -> "↓ Needs Improvement"
        }
    }
}

/**
 * Adherence category enum
 */
enum class AdherenceCategory {
    EXCELLENT,  // ≥95%
    GOOD,       // 80-94%
    FAIR,       // 60-79%
    POOR        // <60%
}
