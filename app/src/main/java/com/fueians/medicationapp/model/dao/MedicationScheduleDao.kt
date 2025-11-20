package com.fueians.medicationapp.model.dao

import androidx.room.*
import com.fueians.medicationapp.model.entities.MedicationScheduleEntity
import kotlinx.coroutines.flow.Flow

/**
 * MedicationScheduleDao
 *
 * Responsibility: Provide database access methods for medication schedules.
 *
 * This DAO interface defines all database operations for MedicationScheduleEntity,
 * enabling queries for scheduled doses, missed doses, and today's schedules.
 * Uses Flow for reactive data updates.
 *
 * Related Classes: MedicationScheduleEntity, AppDatabase
 */
@Dao
interface MedicationScheduleDao {

    /**
     * Get schedules by medication
     * Returns all schedules for a specific medication
     *
     * @param medicationId Medication ID
     */
    @Query("SELECT * FROM medication_schedules WHERE medication_id = :medicationId")
    suspend fun getSchedulesByMedication(medicationId: String): Flow<List<MedicationScheduleEntity>>

    /**
     * Get today's schedules
     * Returns all schedules for the current date
     */
    @Query("SELECT * FROM medication_schedules WHERE DATE(scheduled_time / 1000, 'unixepoch') = DATE('now')")
    suspend fun getTodaySchedules(): Flow<List<MedicationScheduleEntity>>

    /**
     * Insert a schedule
     *
     * @param schedule Schedule to insert
     */
    @Insert
    suspend fun insertSchedule(schedule: MedicationScheduleEntity)

    /**
     * Update a schedule
     *
     * @param schedule Schedule to update
     */
    @Update
    suspend fun updateSchedule(schedule: MedicationScheduleEntity)

    /**
     * Delete a schedule
     *
     * @param schedule Schedule to delete
     */
    @Delete
    suspend fun deleteSchedule(schedule: MedicationScheduleEntity)

    /**
     * Get missed schedules
     * Returns all schedules that are not taken and past their scheduled time
     *
     * @param currentTime Current timestamp
     */
    @Query("SELECT * FROM medication_schedules WHERE is_taken = 0 AND scheduled_time < :currentTime")
    suspend fun getMissedSchedules(currentTime: Long): Flow<List<MedicationScheduleEntity>>
}