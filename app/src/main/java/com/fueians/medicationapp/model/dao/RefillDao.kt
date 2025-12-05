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

    // ========== INSERT ==========
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRefill(refill: RefillEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRefills(refills: List<RefillEntity>)

    // ========== UPDATE ==========
    @Update
    suspend fun updateRefill(refill: RefillEntity)

    // ========== DELETE ==========
    @Delete
    suspend fun deleteRefill(refill: RefillEntity)

    @Query("DELETE FROM refills WHERE id = :id")
    suspend fun deleteRefillById(id: String)

    // ========== QUERY - FLOW (Reactive) ==========
    @Query("SELECT * FROM refills WHERE medication_id = :medicationId")
    fun getRefillByMedicationIdFlow(medicationId: String): Flow<RefillEntity?>

    @Query("SELECT * FROM refills WHERE reminder_enabled = 1 ORDER BY next_refill_date ASC")
    fun getRefillsWithReminders(): Flow<List<RefillEntity>>

    @Query("""
        SELECT * FROM refills 
        WHERE next_refill_date <= :thresholdDate 
        AND reminder_enabled = 1 
        ORDER BY next_refill_date ASC
    """)
    fun getUpcomingRefills(thresholdDate: Long): Flow<List<RefillEntity>>

    // ========== QUERY - SYNC (Non-reactive) ==========
    @Query("SELECT * FROM refills WHERE id = :id")
    suspend fun getRefillById(id: String): RefillEntity?

    @Query("SELECT * FROM refills WHERE medication_id = :medicationId")
    suspend fun getRefillByMedicationId(medicationId: String): RefillEntity?

    @Query("SELECT * FROM refills ORDER BY next_refill_date ASC")
    suspend fun getAllRefillsSync(): List<RefillEntity>

    @Query("""
        SELECT * FROM refills 
        WHERE next_refill_date <= :currentTime 
        ORDER BY next_refill_date ASC
    """)
    suspend fun getOverdueRefills(currentTime: Long = System.currentTimeMillis()): List<RefillEntity>

    @Query("""
        SELECT * FROM refills 
        WHERE next_refill_date BETWEEN :currentTime AND :futureTime 
        ORDER BY next_refill_date ASC
    """)
    suspend fun getRefillsDueSoon(
        currentTime: Long = System.currentTimeMillis(),
        futureTime: Long = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L) // 7 days
    ): List<RefillEntity>

    @Query("""
        SELECT * FROM refills 
        WHERE remaining_quantity <= (total_quantity * 0.25) 
        ORDER BY remaining_quantity ASC
    """)
    suspend fun getLowQuantityRefills(): List<RefillEntity>

    // ========== STATISTICS ==========
    @Query("SELECT COUNT(*) FROM refills WHERE next_refill_date <= :currentTime")
    suspend fun getOverdueRefillCount(currentTime: Long = System.currentTimeMillis()): Int

    @Query("SELECT COUNT(*) FROM refills WHERE remaining_quantity <= (total_quantity * 0.25)")
    suspend fun getLowQuantityCount(): Int
}