package com.fueians.medicationapp.model.entities
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.TypeConverters
import androidx.room.Index
import kotlinx.serialization.Serializable

/**
 * Interaction severity levels
 */
enum class InteractionSeverity {
    MINOR,      // Minimal clinical significance
    MODERATE,   // May require monitoring or intervention
    MAJOR,      // Potentially dangerous, avoid combination
    SEVERE      // Life-threatening, contraindicated
}

/**
 * DrugInteractionEntity
 *
 * Responsibility: Represent drug interaction data.
 *
 * This entity caches drug-to-drug interaction information retrieved from
 * external drug interaction databases. It stores interaction details including
 * severity levels, potential effects, and clinical recommendations to help
 * prevent adverse drug interactions.
 *
 * Related Classes: DrugInfoDao, DrugInteractionService
 */
@Serializable
@Entity(
    tableName = "drug_interactions",
    indices = [
        Index(value = ["drug1_id"]),
        Index(value = ["drug2_id"]),
        Index(value = ["severity"]),
        Index(value = ["cached_at"]),
        Index(value = ["drug1_id", "drug2_id"], unique = true)
    ]
)

data class DrugInteractionEntity(
    /**
     * Interaction ID - Primary key
     * Unique identifier for this drug interaction
     */
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    /**
     * First drug ID
     * References the ID of the first drug in the interaction
     * (corresponds to DrugInfoEntity.id or MedicationEntity.id)
     */
    @ColumnInfo(name = "drug1_id")
    val drug1Id: String,

    /**
     * Second drug ID
     * References the ID of the second drug in the interaction
     * (corresponds to DrugInfoEntity.id or MedicationEntity.id)
     */
    @ColumnInfo(name = "drug2_id")
    val drug2Id: String,

    /**
     * First drug name
     * Display name of the first drug
     */
    @ColumnInfo(name = "drug1_name")
    val drug1Name: String,

    /**
     * Second drug name
     * Display name of the second drug
     */
    @ColumnInfo(name = "drug2_name")
    val drug2Name: String,

    /**
     * Severity level
     * Clinical severity: MINOR, MODERATE, MAJOR, or SEVERE
     */
    @ColumnInfo(name = "severity")
    val severity: String,

    /**
     * Interaction description
     * Detailed explanation of how the drugs interact
     */
    @ColumnInfo(name = "description")
    val description: String,

    /**
     * Potential effects
     * List of possible adverse effects from the interaction
     * (e.g., ["increased bleeding risk", "reduced effectiveness", "drowsiness"])
     */
    @ColumnInfo(name = "effects")
    val effects: List<String> = emptyList(),

    /**
     * Clinical recommendations
     * Guidance on managing or avoiding the interaction
     * (e.g., "Monitor blood pressure closely", "Separate doses by 2 hours")
     */
    @ColumnInfo(name = "recommendations")
    val recommendations: String,

    /**
     * Cache timestamp
     * Unix timestamp (milliseconds) when this interaction was cached
     */
    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis()
) {
    /**
     * Relationships (defined in class diagram):
     * - 0..* DrugInteractionEntity involves 0..* MedicationEntity
     * - DrugInteractionEntity managed by DrugInfoDao
     * - DrugInteractionEntity processed by DrugInteractionService
     *
     * Managed by:
     * - DrugInfoDao: Database operations (CRUD)
     * - DrugInteractionService: Checking and processing interactions
     * - DrugAPIClient: External API calls for interaction data
     * - DrugInteractionPresenter: Displaying interactions to user
     */

    companion object {
        // Cache validity duration (30 days in milliseconds)
        const val CACHE_VALIDITY_DURATION = 30L * 24 * 60 * 60 * 1000L
    }

    /**
     * Helper function to get severity as enum
     */
    fun getSeverityLevel(): InteractionSeverity {
        return try {
            InteractionSeverity.valueOf(severity.uppercase())
        } catch (e: IllegalArgumentException) {
            InteractionSeverity.MINOR
        }
    }

    /**
     * Helper function to check if interaction is critical
     * Returns true for MAJOR or SEVERE severity
     */
    fun isCritical(): Boolean {
        val level = getSeverityLevel()
        return level == InteractionSeverity.MAJOR || level == InteractionSeverity.SEVERE
    }

    /**
     * Helper function to check if interaction requires immediate attention
     */
    fun requiresImmediateAttention(): Boolean {
        return getSeverityLevel() == InteractionSeverity.SEVERE
    }

    /**
     * Helper function to check if cached data is still valid
     */
    fun isCacheValid(validityDuration: Long = CACHE_VALIDITY_DURATION): Boolean {
        val now = System.currentTimeMillis()
        return (now - cachedAt) < validityDuration
    }

    /**
     * Helper function to check if cache needs refresh
     */
    fun needsRefresh(validityDuration: Long = CACHE_VALIDITY_DURATION): Boolean {
        return !isCacheValid(validityDuration)
    }

    /**
     * Helper function to get formatted interaction summary
     */
    fun getInteractionSummary(): String {
        return "$drug1Name + $drug2Name: ${getSeverityLevel().name} severity"
    }

    /**
     * Helper function to check if a drug ID is involved in this interaction
     */
    fun involvesDrug(drugId: String): Boolean {
        return drug1Id == drugId || drug2Id == drugId
    }

    /**
     * Helper function to get the other drug ID given one drug ID
     */
    fun getOtherDrugId(drugId: String): String? {
        return when (drugId) {
            drug1Id -> drug2Id
            drug2Id -> drug1Id
            else -> null
        }
    }

    /**
     * Helper function to get the other drug name given one drug ID
     */
    fun getOtherDrugName(drugId: String): String? {
        return when (drugId) {
            drug1Id -> drug2Name
            drug2Id -> drug1Name
            else -> null
        }
    }

    /**
     * Helper function to get severity color code for UI
     */
    fun getSeverityColor(): String {
        return when (getSeverityLevel()) {
            InteractionSeverity.MINOR -> "#FFA726"      // Orange
            InteractionSeverity.MODERATE -> "#FF7043"   // Deep Orange
            InteractionSeverity.MAJOR -> "#EF5350"      // Red
            InteractionSeverity.SEVERE -> "#C62828"     // Dark Red
        }
    }

    /**
     * Helper function to get priority score for sorting
     * Higher score = more critical interaction
     */
    fun getPriorityScore(): Int {
        return when (getSeverityLevel()) {
            InteractionSeverity.MINOR -> 1
            InteractionSeverity.MODERATE -> 2
            InteractionSeverity.MAJOR -> 3
            InteractionSeverity.SEVERE -> 4
        }
    }

    /**
     * Helper function to check if interaction involves specific drug pair
     */
    fun involvesInteraction(drugIdA: String, drugIdB: String): Boolean {
        return (drug1Id == drugIdA && drug2Id == drugIdB) ||
                (drug1Id == drugIdB && drug2Id == drugIdA)
    }

    /**
     * Helper function to format full interaction warning message
     */
    fun getWarningMessage(): String {
        val severityText = getSeverityLevel().name
        val effectsText = if (effects.isNotEmpty()) {
            "\n\nPotential Effects:\n${effects.joinToString("\n") { "• $it" }}"
        } else ""

        return """
            ⚠️ $severityText DRUG INTERACTION DETECTED
            
            Drugs: $drug1Name and $drug2Name
            
            Description: $description$effectsText
            
            Recommendations: $recommendations
        """.trimIndent()
    }
}