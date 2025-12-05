package com.fueians.medicationapp.model.dao

import androidx.room.*
import com.fueians.medicationapp.model.entities.MedicationEntity
import kotlinx.coroutines.flow.Flow

/**
 * MedicationDao
 *
 * Responsibility: Provide database access methods for medication data.
 *
 * This DAO interface defines all database operations for MedicationEntity,
 * including CRUD operations, queries, and search functionality. Uses Flow
 * for reactive data updates.
 *
 * Related Classes: MedicationEntity, AppDatabase
 */
@Dao
interface MedicationDao {

    // ========== INSERT ==========
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: MedicationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedications(medications: List<MedicationEntity>)

    // ========== UPDATE ==========
    @Update
    suspend fun updateMedication(medication: MedicationEntity)

    // ========== DELETE ==========
    @Delete
    suspend fun deleteMedication(medication: MedicationEntity)

    @Query("DELETE FROM medications WHERE id = :id")
    suspend fun deleteMedicationById(id: String)

    // ========== QUERY - FLOW (Reactive) ==========
    @Query("SELECT * FROM medications WHERE user_id = :userId ORDER BY created_at DESC")
    fun getAllMedications(userId: String): Flow<List<MedicationEntity>>

    @Query("SELECT * FROM medications WHERE user_id = :userId AND is_active = 1 ORDER BY created_at DESC")
    fun getActiveMedications(userId: String): Flow<List<MedicationEntity>>

    @Query("SELECT * FROM medications WHERE user_id = :userId AND name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchMedications(userId: String, query: String): Flow<List<MedicationEntity>>

    // ========== QUERY - SYNC (Non-reactive) ==========
    @Query("SELECT * FROM medications WHERE id = :id")
    suspend fun getMedicationById(id: String): MedicationEntity?

    @Query("SELECT * FROM medications ORDER BY created_at DESC")
    suspend fun getAllMedicationsSync(): List<MedicationEntity>

    @Query("SELECT * FROM medications WHERE user_id = :userId ORDER BY created_at DESC")
    suspend fun getMedicationsByUserSync(userId: String): List<MedicationEntity>

    @Query("SELECT * FROM medications WHERE user_id = :userId AND is_active = 1")
    suspend fun getActiveMedicationsSync(userId: String): List<MedicationEntity>

    @Query("SELECT * FROM medications WHERE is_active = 1 AND end_date IS NOT NULL AND end_date < :currentTime")
    suspend fun getExpiredMedications(currentTime: Long): List<MedicationEntity>

    // ========== STATISTICS ==========
    @Query("SELECT COUNT(*) FROM medications WHERE user_id = :userId AND is_active = 1")
    suspend fun getActiveMedicationCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM medications WHERE user_id = :userId")
    suspend fun getTotalMedicationCount(userId: String): Int
}