package com.fueians.medicationapp.model.dao

import androidx.room.*
import com.fueians.medicationapp.model.entities.DrugInfoEntity
import com.fueians.medicationapp.model.entities.DrugInteractionEntity
import kotlinx.coroutines.flow.Flow

/**
 * DrugInfoDao
 *
 * Responsibility: Provide database access methods for drug information and interactions.
 *
 * This DAO interface defines all database operations for DrugInfoEntity and
 * DrugInteractionEntity, including queries for drug details and interaction
 * checking. Uses Flow for reactive data updates.
 *
 * Related Classes: DrugInfoEntity, DrugInteractionEntity, AppDatabase
 */
@Dao
interface DrugInfoDao {

    /**
     * Get drug by ID
     * Returns a Flow that emits the drug info or null
     *
     * @param id Drug ID
     */
    @Query("SELECT * FROM drug_info WHERE id = :id")
    suspend fun getDrugById(id: String): Flow<DrugInfoEntity?>

    /**
     * Search drugs by name
     * Uses LIKE query for partial matching
     *
     * @param query Search query (use '%query%' for contains search)
     */
    @Query("SELECT * FROM drug_info WHERE name LIKE :query")
    suspend fun searchDrugs(query: String): Flow<List<DrugInfoEntity>>

    /**
     * Insert drug information
     * Replaces if drug with same ID already exists
     *
     * @param drugInfo Drug info to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrugInfo(drugInfo: DrugInfoEntity)

    /**
     * Get interactions by drug ID
     * Returns all interactions where the drug is involved (either as drug1 or drug2)
     *
     * @param drugId Drug ID
     */
    @Query("SELECT * FROM drug_interactions WHERE drug1_id = :drugId OR drug2_id = :drugId")
    suspend fun getInteractionsByDrug(drugId: String): Flow<List<DrugInteractionEntity>>
}