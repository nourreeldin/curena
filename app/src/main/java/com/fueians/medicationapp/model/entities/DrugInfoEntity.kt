package com.fueians.medicationapp.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Index
import com.fueians.medicationapp.model.typeconverters.*
import kotlinx.serialization.Serializable

/**
 * DrugInfoEntity
 *
 * Responsibility: Represent drug information from external API.
 *
 * This entity caches drug information retrieved from external drug databases
 * (e.g., FDA API, RxNorm, OpenFDA). It stores comprehensive drug details
 * including names, descriptions, side effects, and warnings to reduce API
 * calls and enable offline access.
 *
 * Related Classes: DrugInfoDao, DrugInfoRepository
 */
@TypeConverters(StringListConverters::class)
@Serializable
@Entity(
    tableName = "drug_info",
    indices = [
        Index(value = ["name"]),
        Index(value = ["generic_name"]),
        Index(value = ["cached_at"])
    ]
)

data class DrugInfoEntity(
    /**
     * Drug ID - Primary key
     * Unique identifier for the drug (e.g., RxCUI, NDC code)
     */
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    /**
     * Drug name
     * Primary name of the drug (could be brand or generic)
     */
    @ColumnInfo(name = "name")
    val name: String,

    /**
     * Generic name (optional)
     * International Non-proprietary Name (INN) or generic name
     * (e.g., "acetaminophen" for Tylenol)
     */
    @ColumnInfo(name = "generic_name")
    val genericName: String? = null,

    /**
     * Brand names
     * List of brand/trade names for this drug
     * (e.g., ["Tylenol", "Panadol", "Paracetamol"])
     */
    @ColumnInfo(name = "brand_names")
    val brandNames: List<String> = emptyList(),

    /**
     * Drug description
     * Detailed description of the drug, its uses, and how it works
     */
    @ColumnInfo(name = "description")
    val description: String,

    /**
     * Side effects
     * List of known side effects and adverse reactions
     * (e.g., ["nausea", "dizziness", "headache"])
     */
    @ColumnInfo(name = "side_effects")
    val sideEffects: List<String> = emptyList(),

    /**
     * Warnings
     * List of important warnings, contraindications, and precautions
     * (e.g., ["Do not take if pregnant", "May cause drowsiness"])
     */
    @ColumnInfo(name = "warnings")
    val warnings: List<String> = emptyList(),

    /**
     * Cache timestamp
     * Unix timestamp (milliseconds) when this information was cached
     * Used to determine when to refresh data from API
     */
    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis()
) {
    /**
     * Relationships (defined in class diagram):
     * - DrugInfoEntity managed by DrugInfoDao
     * - DrugInfoEntity fetched and cached by DrugInfoRepository
     * - DrugInfoEntity sourced from DrugAPIClient
     *
     * Managed by:
     * - DrugInfoDao: Database operations (CRUD)
     * - DrugInfoRepository: Caching and retrieval logic
     * - DrugAPIClient: External API calls for drug information
     * - Used by DrugInteractionPresenter for displaying drug details
     */

    companion object {
        // Cache validity duration (7 days in milliseconds)
        const val CACHE_VALIDITY_DURATION = 7L * 24 * 60 * 60 * 1000L
    }

    /**
     * Helper function to check if cached data is still valid
     * Default cache validity: 7 days
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
     * Helper function to get age of cached data in days
     */
    fun getCacheAgeInDays(): Long {
        val now = System.currentTimeMillis()
        return (now - cachedAt) / (24 * 60 * 60 * 1000L)
    }

    /**
     * Helper function to get all names (primary + brand names)
     */
    fun getAllNames(): List<String> {
        val allNames = mutableListOf(name)
        genericName?.let { allNames.add(it) }
        allNames.addAll(brandNames)
        return allNames.distinct()
    }

    /**
     * Helper function to check if this drug matches a search query
     */
    fun matchesQuery(query: String): Boolean {
        val lowerQuery = query.lowercase()
        return name.lowercase().contains(lowerQuery) ||
                genericName?.lowercase()?.contains(lowerQuery) == true ||
                brandNames.any { it.lowercase().contains(lowerQuery) }
    }

    /**
     * Helper function to get summary info
     */
    fun getSummary(): String {
        val names = mutableListOf<String>()
        genericName?.let { names.add("Generic: $it") }
        if (brandNames.isNotEmpty()) {
            names.add("Brands: ${brandNames.joinToString(", ")}")
        }
        return if (names.isNotEmpty()) {
            "${name} (${names.joinToString(" | ")})"
        } else {
            name
        }
    }

    /**
     * Helper function to check if drug has critical warnings
     */
    fun hasCriticalWarnings(): Boolean {
        return warnings.isNotEmpty()
    }

    /**
     * Helper function to count total safety concerns
     */
    fun getSafetyConcernsCount(): Int {
        return sideEffects.size + warnings.size
    }
}
