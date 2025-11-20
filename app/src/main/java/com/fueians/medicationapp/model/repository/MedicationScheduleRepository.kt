package com.fueians.medicationapp.model.repository

import com.fueians.medicationapp.model.dao.MedicationScheduleDao
import com.fueians.medicationapp.model.entities.MedicationScheduleEntity
import com.fueians.medicationapp.model.remote.SupabaseClient
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers

class MedicationScheduleRepository(
    private val medicationScheduleDao: MedicationScheduleDao,
    private val supabaseClient: SupabaseClient
) {
    private val ioScheduler = Schedulers.io()

    // 1. Observation Method (Consolidated to include network sync)
    fun observeSchedulesByMedication(medicationId: String): Flowable<List<MedicationScheduleEntity>> {
        // Network sync operation (should return a Completable after running)
        val syncCompletable = supabaseClient.fetchSchedulesByMedication(medicationId)
            .flatMapCompletable { remoteList ->
                // ⚠️ REPLACE COMMENT with actual bulk insert logic in your DAO
                Completable.fromAction { /* medicationScheduleDao.syncSchedules(remoteList) */ }
            }
            .subscribeOn(ioScheduler)

        // Return the local cache Flowable, but trigger network sync on subscription
        return medicationScheduleDao.getSchedulesByMedication(medicationId)
            .subscribeOn(ioScheduler)
            .doOnSubscribe {
                syncCompletable.subscribe({}, { error -> println("Schedule sync failed: $error") })
            }
    }

    // 2. Observation Method (Local-Only - Today's Schedules)
    fun observeTodaySchedules(startOfDay: Long, endOfDay: Long): Flowable<List<MedicationScheduleEntity>> {
        // This is typically local-only, but you might still trigger a sync here if necessary.
        return medicationScheduleDao.getTodaySchedules(startOfDay, endOfDay)
            .subscribeOn(ioScheduler)
    }

    // 3. Observation Method (Local-Only - Missed Schedules)
    fun observeMissedSchedules(currentTime: Long): Flowable<List<MedicationScheduleEntity>> {
        // This is typically local-only.
        return medicationScheduleDao.getMissedSchedules(currentTime)
            .subscribeOn(ioScheduler)
    }

    // 4. Save/Insert Method (Consolidated to call DAO AND Supabase)
    fun saveSchedule(schedule: MedicationScheduleEntity): Completable {
        // Perform local insert AND remote upload sequentially
        return medicationScheduleDao.insertSchedule(schedule)
            .andThen(supabaseClient.uploadSchedule(schedule))
            .subscribeOn(ioScheduler)
    }

    // 5. Update Method (Consolidated to call DAO only, but could add remote update if needed)
    fun updateSchedule(schedule: MedicationScheduleEntity): Completable {
        // You should add remote update here if required for synchronization
        return medicationScheduleDao.updateSchedule(schedule)
            .subscribeOn(ioScheduler)
    }

    // 6. Delete Method (Consolidated to call DAO AND Supabase)
    fun deleteSchedule(schedule: MedicationScheduleEntity): Completable {
        // Perform local delete AND remote delete sequentially
        return medicationScheduleDao.deleteSchedule(schedule)
            .andThen(supabaseClient.deleteSchedule(schedule.id))
            .subscribeOn(ioScheduler)
    }
}