package com.fueians.medicationapp.model.repository

import com.fueians.medicationapp.model.dao.MedicationDao
import com.fueians.medicationapp.model.entities.MedicationEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import com.fueians.medicationapp.model.remote.SupabaseClient


class MedicationRepository(
    private val medicationDao: MedicationDao,
    private val supabaseClient: SupabaseClient // ⚠️ New Dependency
) {
    private val ioScheduler = Schedulers.io()

    fun observeAllMedications(userId: String): Flowable<List<MedicationEntity>> {
        // 1. Define the network fetch: Fetches from Supabase and saves to local DB
        val networkFetchCompletable = supabaseClient.fetchMedications(userId)
            .flatMapCompletable { remoteList ->
                // Use flatMapCompletable to insert the entire list asynchronously
                Completable.fromAction {
                    medicationDao.insertMedication(remoteList) // Assuming bulk insert method exists
                }
            }
            .subscribeOn(ioScheduler)

        // 2. Combine Local Cache and Network operations
        return medicationDao.getAllMedications() // Always start observing local cache
            .subscribeOn(ioScheduler)
            .doOnSubscribe {
                // When subscription starts, trigger the network sync
                networkFetchCompletable.subscribe({}, { error ->
                    // Handle sync errors gracefully
                    println("Supabase sync failed: ${error.message}")
                })
            }
    }

    fun observeAllMedications(): Flowable<List<MedicationEntity>> {
        return medicationDao.getAllMedications()
            .subscribeOn(ioScheduler)
    }

    fun fetchMedicationById(id: String): Single<MedicationEntity> {
        return medicationDao.getMedicationById(id)
            .subscribeOn(ioScheduler)
    }

    fun observeSearchMedications(query: String): Flowable<List<MedicationEntity>> {
        return medicationDao.searchMedications(query)
            .subscribeOn(ioScheduler)
    }

    fun saveMedication(medication: MedicationEntity): Completable {
        return medicationDao.insertMedication(medication)
            .subscribeOn(ioScheduler)
    }

    fun updateMedication(medication: MedicationEntity): Completable {
        return medicationDao.updateMedication(medication)
            .subscribeOn(ioScheduler)
    }

    fun deleteMedication(medication: MedicationEntity): Completable {
        return medicationDao.deleteMedication(medication)
            .subscribeOn(ioScheduler)
    }
}