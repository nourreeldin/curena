package com.fueians.medicationapp.model.repository

import com.fueians.medicationapp.model.dao.AdherenceLogDao
import com.fueians.medicationapp.model.dao.ScheduleDao
import com.fueians.medicationapp.model.entities.AdherenceLog
import com.fueians.medicationapp.model.entities.MedicationSchedule
import com.fueians.medicationapp.presenter.TestRepo.NotificationSettings
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Calendar

/**
 * ScheduleRepository
 *
 * Responsibility: Provide a clean, RxJava-based API for schedule and adherence data operations.
 */
class ScheduleRepository {

    // DAOs are now private attributes with placeholder implementations.
    private val scheduleDao: ScheduleDao = object : ScheduleDao {
        private val schedules = mutableMapOf<String, MedicationSchedule>()
        override fun getAllSchedules(): Flowable<List<MedicationSchedule>> = Flowable.just(schedules.values.toList())
        override fun insertSchedule(schedule: MedicationSchedule): Completable = Completable.fromAction { schedules[schedule.id] = schedule }
        override fun updateSchedule(schedule: MedicationSchedule): Completable = Completable.fromAction { schedules[schedule.id] = schedule }
        override fun deleteScheduleById(scheduleId: String): Completable = Completable.fromAction { schedules.remove(scheduleId) }
    }

    private val adherenceLogDao: AdherenceLogDao = object : AdherenceLogDao {
        private val logs = mutableListOf<AdherenceLog>()
        override fun getLogsForPatient(patientId: String): Flowable<List<AdherenceLog>> = Flowable.just(emptyList()) // Simplified
        override fun insertLog(log: AdherenceLog): Completable = Completable.fromAction { logs.add(log) }
        override fun getMissedDoses(): Flowable<List<AdherenceLog>> = Flowable.just(logs.filter { it.status == "MISSED" })
    }

    private val backgroundScheduler = Schedulers.io()

    fun loadSchedules(): Flowable<List<MedicationSchedule>> {
        return scheduleDao.getAllSchedules().subscribeOn(backgroundScheduler)
    }

    fun loadTodaySchedules(): Flowable<List<MedicationSchedule>> {
        val startOfDay = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0) }.timeInMillis
        val endOfDay = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59) }.timeInMillis
        return scheduleDao.getAllSchedules()
            .map { it.filter { schedule -> schedule.timeToTake in startOfDay..endOfDay } }
            .subscribeOn(backgroundScheduler)
    }

    fun createSchedule(schedule: MedicationSchedule): Completable {
        return scheduleDao.insertSchedule(schedule).subscribeOn(backgroundScheduler)
    }

    fun updateSchedule(schedule: MedicationSchedule): Completable {
        return scheduleDao.updateSchedule(schedule).subscribeOn(backgroundScheduler)
    }

    fun deleteSchedule(scheduleId: String): Completable {
        return scheduleDao.deleteScheduleById(scheduleId).subscribeOn(backgroundScheduler)
    }

    fun markDoseTaken(scheduleId: String, timestamp: Long): Completable {
        return adherenceLogDao.insertLog(AdherenceLog(scheduleId = scheduleId, timestamp = timestamp, status = "TAKEN")).subscribeOn(backgroundScheduler)
    }

    fun markDoseMissed(scheduleId: String): Completable {
        return adherenceLogDao.insertLog(AdherenceLog(scheduleId = scheduleId, timestamp = System.currentTimeMillis(), status = "MISSED")).subscribeOn(backgroundScheduler)
    }

    fun loadMissedDoses(): Flowable<List<AdherenceLog>> {
        return adherenceLogDao.getMissedDoses().subscribeOn(backgroundScheduler)
    }

    fun updateNotificationSettings(settings: NotificationSettings): Completable {
        return Completable.fromAction { println("Settings updated: $settings") }.subscribeOn(backgroundScheduler)
    }
}
