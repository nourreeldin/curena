package com.fueians.medicationapp.model.dao
import androidx.room.*
import com.fueians.medicationapp.model.entities.DrugInteractionEntity

@Dao
interface DrugInteractionDao {

    // Insert a single interaction (replace if conflict on primary key)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(interaction: DrugInteractionEntity)

    // Insert multiple interactions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(interactions: List<DrugInteractionEntity>)

    // Get all interactions involving a specific drug
    @Query("SELECT * FROM drug_interactions WHERE drug1_id = :drugId OR drug2_id = :drugId")
    suspend fun getInteractionsForDrug(drugId: String): List<DrugInteractionEntity>

    // Get a specific interaction between two drugs
    @Query("""
        SELECT * FROM drug_interactions
        WHERE (drug1_id = :drugIdA AND drug2_id = :drugIdB)
           OR (drug1_id = :drugIdB AND drug2_id = :drugIdA)
        LIMIT 1
    """)
    suspend fun getInteractionBetween(drugIdA: String, drugIdB: String): DrugInteractionEntity?

    // Get interactions that need cache refresh
    @Query("SELECT * FROM drug_interactions WHERE cached_at < :expiryTime")
    suspend fun getExpiredInteractions(expiryTime: Long): List<DrugInteractionEntity>

    // Delete an interaction
    @Delete
    suspend fun delete(interaction: DrugInteractionEntity)

    // Delete all interactions
    @Query("DELETE FROM drug_interactions")
    suspend fun clearAll()
}
