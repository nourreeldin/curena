package com.fueians.medicationapp.model.repository

import android.content.Context
import com.fueians.medicationapp.model.dao.MedicationScheduleDao
import com.fueians.medicationapp.model.dao.AdherenceLogDao
import com.fueians.medicationapp.model.dao.MedicationDao
import com.fueians.medicationapp.model.clients.SupabaseClient
import com.fueians.medicationapp.model.database.AppDatabase
import com.fueians.medicationapp.model.entities.MedicationScheduleEntity
import com.fueians.medicationapp.model.entities.AdherenceLogEntity
import com.fueians.medicationapp.model.entities.AdherenceStatus
import com.fueians.medicationapp.model.entities.MedicationEntity
import com.fueians.medicationapp.model.services.ReminderService
import com.fueians.medicationapp.model.services.NotificationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.*

class ScheduleRepository(context: Context) {

    private val scheduleDao: MedicationScheduleDao by lazy { AppDatabase.getInstance(context).medicationScheduleDao() }
    private val adherenceLogDao: AdherenceLogDao by lazy { AppDatabase.getInstance(context).adherenceLogDao() }
    private val supabaseClient: SupabaseClient by lazy { SupabaseClient() }
    private val reminderService: ReminderService by lazy { ReminderService(context) }
    private val notificationService: NotificationService by lazy { NotificationService(context) }

    // ---------------------------------------------------------
    // Get all schedules
    // ---------------------------------------------------------
    suspend fun getAllSchedules(): List<MedicationScheduleEntity> =
        scheduleDao.getTodaySchedules().first()

    // ---------------------------------------------------------
    // Get schedule by ID
    // ---------------------------------------------------------
    suspend fun getScheduleById(id: String): MedicationScheduleEntity? =
        scheduleDao.getSchedulesByMedication(id).first().firstOrNull()

    // ---------------------------------------------------------
    // Create a new schedule (local + reminder)
    // ---------------------------------------------------------
    suspend fun createSchedule(schedule: MedicationScheduleEntity) = withContext(Dispatchers.IO) {
        scheduleDao.insertSchedule(schedule)
        val medication = getMedicationForSchedule(schedule)
        reminderService.scheduleReminder(schedule, medication)
    }

    // ---------------------------------------------------------
    // Update schedule
    // ---------------------------------------------------------
    suspend fun updateSchedule(schedule: MedicationScheduleEntity) = withContext(Dispatchers.IO) {
        scheduleDao.updateSchedule(schedule)
        val medication = getMedicationForSchedule(schedule)
        reminderService.rescheduleReminder(schedule, medication)
    }

    // ---------------------------------------------------------
    // Delete schedule
    // ---------------------------------------------------------
    suspend fun deleteSchedule(schedule: MedicationScheduleEntity) = withContext(Dispatchers.IO) {
        reminderService.cancelReminder(schedule)
        scheduleDao.deleteSchedule(schedule)
    }

    // ---------------------------------------------------------
    // Mark a dose as taken
    // ---------------------------------------------------------
    suspend fun markDoseTaken(userId: String, medicationId: String, schedule: MedicationScheduleEntity) = withContext(Dispatchers.IO) {
        val log = AdherenceLogEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            medicationId = medicationId,
            scheduleId = schedule.id,
            scheduledTime = schedule.scheduledTime,
            takenTime = System.currentTimeMillis(),
            status = AdherenceStatus.TAKEN,
            timestamp = System.currentTimeMillis()
        )
        adherenceLogDao.insertLog(log)

        val medication = getMedicationForSchedule(schedule)
        notificationService.showMedicationReminder(schedule, medication)
    }

    // ---------------------------------------------------------
    // Mark a dose as missed
    // ---------------------------------------------------------
    suspend fun markDoseMissed(userId: String, medicationId: String, schedule: MedicationScheduleEntity) = withContext(Dispatchers.IO) {
        val log = AdherenceLogEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            medicationId = medicationId,
            scheduleId = schedule.id,
            scheduledTime = schedule.scheduledTime,
            takenTime = null,
            status = AdherenceStatus.MISSED,
            timestamp = System.currentTimeMillis()
        )
        adherenceLogDao.insertLog(log)

        val medication = getMedicationForSchedule(schedule)
        notificationService.showMissedDoseAlert(schedule, medication)
    }

    // ---------------------------------------------------------
    // Get all missed doses
    // ---------------------------------------------------------
    suspend fun getMissedDoses(): List<MedicationScheduleEntity> =
        scheduleDao.getMissedSchedules(System.currentTimeMillis()).first()

    // ---------------------------------------------------------
    // Sync schedules with Supabase
    // ---------------------------------------------------------
    suspend fun syncSchedules() = withContext(Dispatchers.IO) {
        try {
            val response = supabaseClient.databaseClient
                .from("medication_schedules")
                .select()
                .decodeList<MedicationScheduleEntity>()

            val remoteSchedules = supabaseClient.databaseClient
                .from("medication_schedules")
                .select()
                .decodeList<MedicationScheduleEntity>()


            val local = scheduleDao.getTodaySchedules().first()
            val merged = mergeAndResolveSchedules(local, remoteSchedules)

            merged.forEach { scheduleDao.insertSchedule(it) }
            merged.forEach { pushScheduleToSupabase(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun mergeAndResolveSchedules(
        local: List<MedicationScheduleEntity>,
        remote: List<MedicationScheduleEntity>
    ): List<MedicationScheduleEntity> {
        val map = local.associateBy { it.id }.toMutableMap()
        remote.forEach { remoteItem ->
            val existing = map[remoteItem.id]
            map[remoteItem.id] = if (existing != null) resolveConflict(existing, remoteItem) else remoteItem
        }
        return map.values.toList()
    }

    private fun resolveConflict(
        local: MedicationScheduleEntity,
        remote: MedicationScheduleEntity
    ): MedicationScheduleEntity {
        return if (remote.scheduledTime > local.scheduledTime) remote else local
    }

    private suspend fun pushScheduleToSupabase(schedule: MedicationScheduleEntity) {
        supabaseClient.databaseClient
            .from("medication_schedules")
            .upsert(schedule)
    }

    // ---------------------------------------------------------
    // Helper to get MedicationEntity for a schedule
    // ---------------------------------------------------------
    private val medicationDao: MedicationDao by lazy { AppDatabase.getInstance(context).medicationDao() }
    private suspend fun getMedicationForSchedule(schedule: MedicationScheduleEntity): MedicationEntity {
        return medicationDao.getMedicationById(schedule.medicationId)
            ?: throw IllegalStateException("Medication not found for schedule ${schedule.id}")
    }
}
