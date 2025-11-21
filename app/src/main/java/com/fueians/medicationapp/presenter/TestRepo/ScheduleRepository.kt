package com.fueians.medicationapp.presenter.TestRepo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Calendar

// =========================================================================
// 1. Placeholder Data & DAO Interfaces
// =========================================================================

data class MedicationSchedule(
    val id: String,
    val medicationId: String,
    val timeToTake: Long, // Epoch timestamp for the scheduled time
    val isRecurring: Boolean
)

data class AdherenceLog(
    val scheduleId: String,
    val timestamp: Long,
    val status: String // e.g., "TAKEN", "MISSED"
)

data class NotificationSettings(val reminderEnabled: Boolean, val reminderTime: Int)

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM MedicationSchedule")
    fun getAllSchedules(): Flow<List<MedicationSchedule>>

    @Insert
    suspend fun insertSchedule(schedule: MedicationSchedule)

    @Update
    suspend fun updateSchedule(schedule: MedicationSchedule)

    @Query("DELETE FROM MedicationSchedule WHERE id = :scheduleId")
    suspend fun deleteScheduleById(scheduleId: String)
}

@Dao
interface AdherenceLogDao {
    @Insert
    suspend fun insertLog(log: AdherenceLog)

    @Query("SELECT * FROM AdherenceLog WHERE status = 'MISSED'")
    fun getMissedDoses(): Flow<List<AdherenceLog>>
}

// =========================================================================
// 2. Schedule Repository
// =========================================================================

class ScheduleRepository(
    private val scheduleDao: ScheduleDao,
    private val adherenceLogDao: AdherenceLogDao,
    private val backgroundDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun loadSchedules(): Flow<List<MedicationSchedule>> {
        return scheduleDao.getAllSchedules()
    }

    fun loadTodaySchedules(): Flow<List<MedicationSchedule>> {
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
        }.timeInMillis

        val endOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
        }.timeInMillis

        return scheduleDao.getAllSchedules().map {
            it.filter { schedule -> schedule.timeToTake in startOfDay..endOfDay }
        }
    }

    suspend fun createSchedule(schedule: MedicationSchedule) = withContext(backgroundDispatcher) {
        scheduleDao.insertSchedule(schedule)
    }

    suspend fun updateSchedule(schedule: MedicationSchedule) = withContext(backgroundDispatcher) {
        scheduleDao.updateSchedule(schedule)
    }

    suspend fun deleteSchedule(scheduleId: String) = withContext(backgroundDispatcher) {
        scheduleDao.deleteScheduleById(scheduleId)
    }

    suspend fun markDoseTaken(scheduleId: String, timestamp: Long) = withContext(backgroundDispatcher) {
        adherenceLogDao.insertLog(AdherenceLog(scheduleId, timestamp, "TAKEN"))
    }

    suspend fun markDoseMissed(scheduleId: String) = withContext(backgroundDispatcher) {
        adherenceLogDao.insertLog(AdherenceLog(scheduleId, System.currentTimeMillis(), "MISSED"))
    }

    fun loadMissedDoses(): Flow<List<AdherenceLog>> {
        return adherenceLogDao.getMissedDoses()
    }

    suspend fun updateNotificationSettings(settings: NotificationSettings) = withContext(backgroundDispatcher) {
        // In a real app, this would likely save to SharedPreferences or a settings DAO.
        println("Settings updated: $settings")
    }
}
