package com.fueians.medicationapp.model.repository

import com.fueians.medicationapp.model.dao.AdherenceLogDao
import com.fueians.medicationapp.model.dao.ScheduleDao
import com.fueians.medicationapp.model.entities.AdherenceLog
import com.fueians.medicationapp.model.entities.MedicationSchedule
import com.fueians.medicationapp.presenter.TestRepo.NotificationSettings
import java.util.Calendar

/**
 * ScheduleRepository
 *
 * Responsibility: Provide a clean, synchronous API for schedule and adherence data operations.
 * All methods in this repository perform blocking I/O and MUST be called from a background thread.
 */
class ScheduleRepository {

    // DAOs are now private attributes with placeholder implementations.
    private val scheduleDao: ScheduleDao = object : ScheduleDao {
        private val schedules = mutableMapOf<String, MedicationSchedule>()
        override fun getAllSchedules(): List<MedicationSchedule> = schedules.values.toList()
        override fun insertSchedule(schedule: MedicationSchedule) { schedules[schedule.id] = schedule }
        override fun updateSchedule(schedule: MedicationSchedule) { schedules[schedule.id] = schedule }
        override fun deleteScheduleById(scheduleId: String) { schedules.remove(scheduleId) }
    }

    private val adherenceLogDao: AdherenceLogDao = object : AdherenceLogDao {
        private val logs = mutableListOf<AdherenceLog>()
        override fun getLogsForPatient(patientId: String): List<AdherenceLog> = emptyList() // Simplified for placeholder
        override fun insertLog(log: AdherenceLog) { logs.add(log) }
        override fun getMissedDoses(): List<AdherenceLog> = logs.filter { it.status == "MISSED" }
    }

    fun loadSchedules(): List<MedicationSchedule> {
        return scheduleDao.getAllSchedules()
    }

    fun loadTodaySchedules(): List<MedicationSchedule> {
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
        }.timeInMillis

        val endOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
        }.timeInMillis

        return scheduleDao.getAllSchedules()
            .filter { schedule -> schedule.timeToTake in startOfDay..endOfDay }
    }

    fun createSchedule(schedule: MedicationSchedule) {
        scheduleDao.insertSchedule(schedule)
    }

    fun updateSchedule(schedule: MedicationSchedule) {
        scheduleDao.updateSchedule(schedule)
    }

    fun deleteSchedule(scheduleId: String) {
        scheduleDao.deleteScheduleById(scheduleId)
    }

    fun markDoseTaken(scheduleId: String, timestamp: Long) {
        adherenceLogDao.insertLog(AdherenceLog(scheduleId = scheduleId, timestamp = timestamp, status = "TAKEN"))
    }

    fun markDoseMissed(scheduleId: String) {
        adherenceLogDao.insertLog(AdherenceLog(scheduleId = scheduleId, timestamp = System.currentTimeMillis(), status = "MISSED"))
    }

    fun loadMissedDoses(): List<AdherenceLog> {
        return adherenceLogDao.getMissedDoses()
    }

    fun updateNotificationSettings(settings: NotificationSettings) {
        // In a real app, this would likely save to SharedPreferences or a settings DAO.
        println("Settings updated: $settings")
    }
}
