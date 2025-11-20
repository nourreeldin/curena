package com.fueians.medicationapp.model.repository

import com.fueians.medicationapp.model.dao.AdherenceLogDao
import com.fueians.medicationapp.model.entities.AdherenceLogEntity
import com.fueians.medicationapp.model.remote.SupabaseClient // ⚠️ NEW IMPORT
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers

class AdherenceLogRepository(
    private val adherenceLogDao: AdherenceLogDao,
    private val supabaseClient: SupabaseClient // ⚠️ NEW DEPENDENCY
) {
    private val ioScheduler = Schedulers.io()

    fun observeLogsByMedication(medicationId: String): Flowable<List<AdherenceLogEntity>> {
        // Trigger network sync on subscription
        val syncCompletable = supabaseClient.fetchAdherenceLogs(medicationId) // Assume this method exists
            .flatMapCompletable { remoteList ->
                // Logic to insert/sync logs (often appended, not fully replaced)
                Completable.fromAction { /* adherenceLogDao.insertLogs(remoteList) */ }
            }
            .subscribeOn(ioScheduler)

        return adherenceLogDao.getLogsByMedication(medicationId)
            .subscribeOn(ioScheduler)
            .doOnSubscribe { syncCompletable.subscribe({}, { error -> println("Adherence log sync failed: $error") }) }
    }

    // Existing methods
    fun saveLog(log: AdherenceLogEntity): Completable {
        return adherenceLogDao.insertLog(log)
            .andThen(supabaseClient.uploadAdherenceLog(log)) // Assume uploadAdherenceLog returns Completable
            .subscribeOn(ioScheduler)
    }

    fun observeLogsByDateRange(startDate: Long, endDate: Long): Flowable<List<AdherenceLogEntity>> {
        return adherenceLogDao.getLogsByDateRange(startDate, endDate)
            .subscribeOn(ioScheduler)
    }

    fun updateLog(log: AdherenceLogEntity): Completable {
        return adherenceLogDao.updateLog(log)
            .subscribeOn(ioScheduler)
    }
}