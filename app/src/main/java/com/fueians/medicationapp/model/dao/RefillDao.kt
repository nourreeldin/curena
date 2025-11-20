package com.fueians.medicationapp.model.dao
import com.fueians.medicationapp.model.entities.RefillEntity
import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * RefillDao
 *
 * Responsibility: Provide database access methods for medication refill tracking.
 *
 * This DAO interface defines all database operations for RefillEntity,
 * enabling queries for refill information, low stock alerts, and refill
 * management. Uses Flow for reactive data updates.
 *
 * Related Classes: RefillEntity, AppDatabase
 */
@Dao
interface RefillDao {

    /**
     * Get refill by medication ID
     * Returns the refill record for a specific medication
     *
     * @param medicationId Medication ID
     */
    @Query("SELECT * FROM refills WHERE medication_id = :medicationId")
    fun getRefillByMedication(medicationId: String): Flow<RefillEntity?>

    /**
     * Insert a refill record
     * Replaces if refill with same ID already exists
     *
     * @param refill Refill to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRefill(refill: RefillEntity)

    /**
     * Update a refill record
     *
     * @param refill Refill to update
     */
    @Update
    suspend fun updateRefill(refill: RefillEntity)

    /**
     * Get low stock refills
     * Returns all refills where remaining quantity is at or below threshold
     *
     * @param threshold Maximum remaining quantity to include
     */
    @Query("SELECT * FROM refills WHERE remaining_quantity <= :threshold")
    suspend fun getLowStockRefills(threshold: Int): Flow<List<RefillEntity>>
}