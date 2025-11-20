package com.fueians.medicationapp.model.repository

import com.fueians.medicationapp.model.dao.CaregiverPatientDao
import com.fueians.medicationapp.model.entities.CaregiverPatientEntity
import com.fueians.medicationapp.model.remote.SupabaseClient
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single // ⚠️ Ensure Single is imported for use with flatMapCompletable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.kotlin.subscribeBy // Useful extension for simple error handling in doOnSubscribe

class CaregiverRepository(
    private val caregiverPatientDao: CaregiverPatientDao,
    private val supabaseClient: SupabaseClient
) {
    private val ioScheduler = Schedulers.io()

    fun observePatientsByCaregiver(caregiverId: String): Flowable<List<CaregiverPatientEntity>> {

        // 1. Define the network sync operation
        // Assuming supabaseClient.fetchCaregiverPatients returns Single<List<...>>
        val syncCompletable = supabaseClient.fetchCaregiverPatients(caregiverId)
            // flatMapCompletable is correctly used here to convert Single<List<T>> into Completable
            .flatMapCompletable { remoteList ->
                // 2. Logic to clear old local data and insert new remoteList
                // You must replace this comment with a call that returns a Completable
                caregiverPatientDao.syncRelationships(remoteList) // ⚠️ Assuming a bulk sync/insert DAO method exists
            }
            .subscribeOn(ioScheduler)

        // 3. Observe local data and trigger sync
        return caregiverPatientDao.getPatientsByCaregiver(caregiverId)
            .subscribeOn(ioScheduler)
            .doOnSubscribe {
                // Use the RxKotlin extension subscribeBy for clean subscription
                syncCompletable.subscribeBy(
                    onError = { error -> println("Caregiver sync failed: ${error.message}") }
                )
            }
    }

    // ... (Other methods remain the same)
}