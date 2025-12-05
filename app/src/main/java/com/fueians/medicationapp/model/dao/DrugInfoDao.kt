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

    // ========== DRUG INFO - INSERT ==========
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDrugInfo(drugInfo: DrugInfoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrugInfoList(drugInfoList: List<DrugInfoEntity>)

    // ========== DRUG INFO - UPDATE ==========
    @Update
    suspend fun updateDrugInfo(drugInfo: DrugInfoEntity)

    // ========== DRUG INFO - DELETE ==========
    @Delete
    suspend fun deleteDrugInfo(drugInfo: DrugInfoEntity)

    @Query("DELETE FROM drug_info WHERE id = :id")
    suspend fun deleteDrugInfoById(id: String)

    // ========== DRUG INFO - QUERY ==========
    @Query("SELECT * FROM drug_info WHERE id = :id")
    fun getDrugById(id: String): DrugInfoEntity?

    @Query("SELECT * FROM drug_info WHERE name LIKE '%' || :query || '%' OR generic_name LIKE '%' || :query || '%'")
    fun searchDrugs(query: String): List<DrugInfoEntity>

    @Query("SELECT * FROM drug_info ORDER BY name ASC")
    suspend fun getAllDrugs(): List<DrugInfoEntity>

    @Query("SELECT * FROM drug_info WHERE cached_at >= :cutoffTime")
    suspend fun getRecentlyCachedDrugs(cutoffTime: Long): List<DrugInfoEntity>

    @Query("DELETE FROM drug_info WHERE cached_at < :cutoffTime")
    suspend fun deleteExpiredCache(cutoffTime: Long)

    // ========== DRUG INTERACTION - INSERT ==========
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInteraction(interaction: DrugInteractionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInteractions(interactions: List<DrugInteractionEntity>)

    // ========== DRUG INTERACTION - UPDATE ==========
    @Update
    suspend fun updateInteraction(interaction: DrugInteractionEntity)

    // ========== DRUG INTERACTION - DELETE ==========
    @Delete
    suspend fun deleteInteraction(interaction: DrugInteractionEntity)

    @Query("DELETE FROM drug_interactions WHERE id = :id")
    suspend fun deleteInteractionById(id: String)

    // ========== DRUG INTERACTION - QUERY ==========
    @Query("SELECT * FROM drug_interactions WHERE id = :id")
    suspend fun getInteractionById(id: String): DrugInteractionEntity?

    @Query("""
        SELECT * FROM drug_interactions 
        WHERE (drug1_name = :drug1 AND drug2_name = :drug2) 
        OR (drug1_name = :drug2 AND drug2_name = :drug1)
    """)
    suspend fun getInteraction(drug1: String, drug2: String): DrugInteractionEntity?

    @Query("SELECT * FROM drug_interactions WHERE drug1_id = :drugId OR drug2_id = :drugId")
    suspend fun getInteractionsForDrug(drugId: String): List<DrugInteractionEntity>

    @Query("SELECT * FROM drug_interactions WHERE severity = :severity ORDER BY drug1_name ASC")
    suspend fun getInteractionsBySeverity(severity: String): List<DrugInteractionEntity>

    @Query("SELECT * FROM drug_interactions ORDER BY severity DESC")
    suspend fun getAllInteractions(): List<DrugInteractionEntity>

    @Query("DELETE FROM drug_interactions WHERE cached_at < :cutoffTime")
    suspend fun deleteExpiredInteractions(cutoffTime: Long)

    // ========== STATISTICS ==========
    @Query("SELECT COUNT(*) FROM drug_info")
    suspend fun getDrugCount(): Int

    @Query("SELECT COUNT(*) FROM drug_interactions")
    suspend fun getInteractionCount(): Int

    @Query("SELECT COUNT(*) FROM drug_interactions WHERE severity = 'MAJOR' OR severity = 'SEVERE'")
    suspend fun getCriticalInteractionCount(): Int
}