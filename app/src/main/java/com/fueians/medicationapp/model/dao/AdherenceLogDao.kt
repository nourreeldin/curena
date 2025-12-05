package com.fueians.medicationapp.model.dao

import androidx.room.*
import com.fueians.medicationapp.model.entities.AdherenceLogEntity
import com.fueians.medicationapp.model.entities.AdherenceStatus
import kotlinx.coroutines.flow.Flow

/**
 * AdherenceLogDao
 *
 * Responsibility: Provide database access methods for adherence tracking.
 *
 * This DAO interface defines all database operations for AdherenceLogEntity,
 * enabling queries for medication adherence history by medication and date
 * range. Uses Flow for reactive data updates.
 *
 * Related Classes: AdherenceLogEntity, AppDatabase
 */
@Dao
interface AdherenceLogDao {

    // ========== INSERT ==========
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AdherenceLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<AdherenceLogEntity>)

    // ========== UPDATE ==========
    @Update
    suspend fun updateLog(log: AdherenceLogEntity)

    // ========== DELETE ==========
    @Delete
    suspend fun deleteLog(log: AdherenceLogEntity)

    @Query("DELETE FROM adherence_logs WHERE id = :id")
    suspend fun deleteLogById(id: String)

    // ========== QUERY - FLOW (Reactive) ==========
    @Query("SELECT * FROM adherence_logs WHERE user_id = :userId ORDER BY timestamp DESC")
    fun getLogsByUser(userId: String): Flow<List<AdherenceLogEntity>>

    @Query("SELECT * FROM adherence_logs WHERE medication_id = :medicationId ORDER BY timestamp DESC")
    fun getLogsByMedication(medicationId: String): Flow<List<AdherenceLogEntity>>

    @Query("""
        SELECT * FROM adherence_logs 
        WHERE user_id = :userId 
        AND scheduled_time >= :startDate 
        AND scheduled_time <= :endDate 
        ORDER BY scheduled_time DESC
    """)
    fun getLogsByDateRange(userId: String, startDate: Long, endDate: Long): Flow<List<AdherenceLogEntity>>

    @Query("SELECT * FROM adherence_logs WHERE user_id = :userId AND status = :status ORDER BY timestamp DESC")
    fun getLogsByStatus(userId: String, status: AdherenceStatus): Flow<List<AdherenceLogEntity>>

    // ========== QUERY - SYNC (Non-reactive) ==========
    @Query("SELECT * FROM adherence_logs WHERE id = :id")
    suspend fun getLogById(id: String): AdherenceLogEntity?

    @Query("SELECT * FROM adherence_logs ORDER BY timestamp DESC")
    suspend fun getAllLogsSync(): List<AdherenceLogEntity>

    @Query("SELECT * FROM adherence_logs WHERE user_id = :userId ORDER BY timestamp DESC")
    suspend fun getLogsByUserSync(userId: String): List<AdherenceLogEntity>

    @Query("""
        SELECT * FROM adherence_logs 
        WHERE user_id = :userId 
        AND medication_id = :medicationId 
        AND scheduled_time >= :startDate 
        AND scheduled_time <= :endDate
    """)
    suspend fun getLogsForMedicationInRange(
        userId: String,
        medicationId: String,
        startDate: Long,
        endDate: Long
    ): List<AdherenceLogEntity>

    // ========== STATISTICS ==========
    @Query("SELECT COUNT(*) FROM adherence_logs WHERE user_id = :userId AND status = 'TAKEN'")
    suspend fun getTakenLogsCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM adherence_logs WHERE user_id = :userId AND status = 'MISSED'")
    suspend fun getMissedLogsCount(userId: String): Int

    @Query("""
        SELECT COUNT(*) FROM adherence_logs 
        WHERE user_id = :userId 
        AND scheduled_time >= :startDate 
        AND scheduled_time <= :endDate
    """)
    suspend fun getTotalLogsInRange(userId: String, startDate: Long, endDate: Long): Int

    @Query("""
        SELECT COUNT(*) FROM adherence_logs 
        WHERE user_id = :userId 
        AND status = 'TAKEN' 
        AND scheduled_time >= :startDate 
        AND scheduled_time <= :endDate
    """)
    suspend fun getTakenLogsInRange(userId: String, startDate: Long, endDate: Long): Int

    @Query("""
        SELECT CAST(COUNT(CASE WHEN status = 'TAKEN' THEN 1 END) AS FLOAT) / 
               COUNT(*) * 100 
        FROM adherence_logs 
        WHERE user_id = :userId 
        AND scheduled_time >= :startDate 
        AND scheduled_time <= :endDate
    """)
    suspend fun getAdherenceRateInRange(userId: String, startDate: Long, endDate: Long): Float?
}