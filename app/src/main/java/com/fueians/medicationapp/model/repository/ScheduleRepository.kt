package com.fueians.medicationapp.model.repository

import io.reactivex.Completable
import com.fueians.medicationapp.model.dao.MedicationScheduleDao
import com.fueians.medicationapp.model.dao.AdherenceLogDao
import com.fueians.medicationapp.model.remote.SupabaseClient
import com.fueians.medicationapp.model.services.ReminderService
import com.fueians.medicationapp.model.services.NotificationService
import java.util.*

// ScheduleRepository: Manages medication schedules, adherence tracking,
// reminders, and server synchronization.
class ScheduleRepository( private val scheduleDao: MedicationScheduleDao,
    private val adherenceLogDao: AdherenceLogDao,
    private val supabaseClient: SupabaseClient,
    private val reminderService: ReminderService,
    private val notificationService: NotificationService)
{
    // Get all stored schedules
    fun getAllSchedules(): Observable<List<MedicationSchedule>> {
        return scheduleDao.getAllSchedules()
    }


    // Get a schedule by ID
    fun getScheduleById(id: String): Observable<MedicationSchedule> {
        return scheduleDao.getScheduleById(id)
    }


    // Get today's schedules
    fun getTodaySchedules(): Observable<List<MedicationSchedule>> {
        val today = Calendar.getInstance().time
        return scheduleDao.getSchedulesForDate(today)
    }


    // Create new schedule (local + reminder)
    fun createSchedule(schedule: MedicationSchedule): Completable {
        return Completable.fromAction {
            scheduleDao.saveSchedule(schedule)
            reminderService.scheduleReminders(schedule)
        }
    }


    // Update schedule
    fun updateSchedule(schedule: MedicationSchedule): Completable {
        return Completable.fromAction {
            scheduleDao.updateSchedule(schedule)
            reminderService.updateReminders(schedule)
        }
    }


    // Delete schedule
    fun deleteSchedule(id: String): Completable {
        return Completable.fromAction {
            val schedule = scheduleDao.getScheduleSync(id)
            reminderService.cancelReminders(schedule)
            scheduleDao.deleteSchedule(id)
        }
    }


    // Mark a dose as taken
    fun markDoseTaken(scheduleId: String, timestamp: Long): Completable {
        return Completable.fromAction {
            adherenceLogDao.logTaken(scheduleId, timestamp)
            notificationService.sendTakenNotification(scheduleId)
        }
    }


    // Mark a dose as missed
    fun markDoseMissed(scheduleId: String): Completable {
        return Completable.fromAction {
            adherenceLogDao.logMissed(scheduleId)
            notificationService.sendMissedNotification(scheduleId)
        }
    }


    // Get all missed doses
    fun getMissedDoses(): Observable<List<MedicationSchedule>> {
        return scheduleDao.getMissedSchedules()
    }


    // Sync all schedules with remote server
    fun syncSchedules(): Completable {
        return supabaseClient.syncSchedules()
            .andThen(scheduleDao.getAllSchedules().firstOrError())
            .flatMapCompletable { schedules ->
                Completable.fromAction {
                    scheduleDao.saveSchedules(schedules)
                }
            }
    }
}
