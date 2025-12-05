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

    // ========== INSERT ==========
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: MedicationScheduleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(schedules: List<MedicationScheduleEntity>)

    // ========== UPDATE ==========
    @Update
    suspend fun updateSchedule(schedule: MedicationScheduleEntity)

    // ========== DELETE ==========
    @Delete
    suspend fun deleteSchedule(schedule: MedicationScheduleEntity)

    @Query("DELETE FROM medication_schedules WHERE id = :id")
    suspend fun deleteScheduleById(id: String)

    // ========== QUERY - FLOW (Reactive) ==========
    @Query("SELECT * FROM medication_schedules WHERE medication_id = :medicationId ORDER BY scheduled_time ASC")
    fun getSchedulesByMedication(medicationId: String): Flow<List<MedicationScheduleEntity>>

    @Query("""
        SELECT * FROM medication_schedules 
        WHERE scheduled_time >= :startOfDay 
        AND scheduled_time <= :endOfDay 
        AND is_taken = 0 
        ORDER BY scheduled_time ASC
    """)
    fun getTodaySchedules(startOfDay: Long = getTodayStart(), endOfDay: Long = getTodayEnd()): Flow<List<MedicationScheduleEntity>>

    @Query("""
        SELECT * FROM medication_schedules 
        WHERE scheduled_time < :currentTime 
        AND is_taken = 0 
        AND is_missed = 0
        ORDER BY scheduled_time ASC
    """)
    fun getMissedSchedules(currentTime: Long): Flow<List<MedicationScheduleEntity>>

    @Query("""
        SELECT * FROM medication_schedules 
        WHERE scheduled_time > :currentTime 
        AND is_taken = 0 
        ORDER BY scheduled_time ASC
    """)
    fun getUpcomingSchedules(currentTime: Long): Flow<List<MedicationScheduleEntity>>

    // ========== QUERY - SYNC (Non-reactive) ==========
    @Query("SELECT * FROM medication_schedules WHERE id = :id")
    suspend fun getScheduleById(id: String): MedicationScheduleEntity?

    @Query("SELECT * FROM medication_schedules ORDER BY scheduled_time ASC")
    suspend fun getAllSchedulesSync(): List<MedicationScheduleEntity>

    @Query("""
        SELECT * FROM medication_schedules 
        WHERE scheduled_time >= :startTime 
        AND scheduled_time <= :endTime
        ORDER BY scheduled_time ASC
    """)
    suspend fun getSchedulesByDateRange(startTime: Long, endTime: Long): List<MedicationScheduleEntity>

    // ========== STATISTICS ==========
    @Query("SELECT COUNT(*) FROM medication_schedules WHERE is_taken = 1")
    suspend fun getTakenSchedulesCount(): Int

    @Query("SELECT COUNT(*) FROM medication_schedules WHERE is_missed = 1")
    suspend fun getMissedSchedulesCount(): Int

    // Helper functions
    private fun getTodayStart(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getTodayEnd(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
        calendar.set(java.util.Calendar.MINUTE, 59)
        calendar.set(java.util.Calendar.SECOND, 59)
        calendar.set(java.util.Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
}
