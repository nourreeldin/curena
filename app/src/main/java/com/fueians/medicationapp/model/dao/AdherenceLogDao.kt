package com.fueians.medicationapp.model.dao

import androidx.room.*
import com.fueians.medicationapp.model.entities.AdherenceLogEntity
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

    /**
     * Get adherence logs by medication
     * Returns logs ordered by timestamp descending (most recent first)
     *
     * @param medicationId Medication ID
     */
    @Query("SELECT * FROM adherence_logs WHERE medication_id = :medicationId ORDER BY timestamp DESC")
    suspend fun getLogsByMedication(medicationId: String): Flow<List<AdherenceLogEntity>>

    /**
     * Get adherence logs by date range
     * Returns all logs within the specified time period
     *
     * @param startDate Start date timestamp
     * @param endDate End date timestamp
     */
    @Query("SELECT * FROM adherence_logs WHERE timestamp BETWEEN :startDate AND :endDate")
    suspend fun getLogsByDateRange(startDate: Long, endDate: Long): Flow<List<AdherenceLogEntity>>

    /**
     * Insert an adherence log
     *
     * @param log Adherence log to insert
     */
    @Insert
    suspend fun insertLog(log: AdherenceLogEntity)

    /**
     * Update an adherence log
     *
     * @param log Adherence log to update
     */
    @Update
    suspend fun updateLog(log: AdherenceLogEntity)
}