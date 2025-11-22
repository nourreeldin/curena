package com.fueians.medicationapp.model.repository

import com.fueians.medicationapp.model.dao.MedicationDao
import com.fueians.medicationapp.model.entities.Medication
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * MedicationRepository
 *
 * Responsibility: Provide a clean, RxJava-based API for medication data operations.
 */
class MedicationRepository {

    // DAO is now a private attribute with a placeholder implementation.
    private val medicationDao: MedicationDao = object : MedicationDao {
        private val inMemoryMedications = mutableMapOf<String, Medication>()

        override fun getMedicationsForUser(userId: String): Flowable<List<Medication>> {
            return Flowable.just(inMemoryMedications.values.filter { it.userId == userId })
        }

        override fun getMedicationById(medicationId: String): Flowable<Medication> {
            return Flowable.fromIterable(inMemoryMedications.values.filter { it.id == medicationId })
        }

        override fun searchMedicationsForUser(userId: String, query: String): Flowable<List<Medication>> {
            return Flowable.just(inMemoryMedications.values.filter { it.userId == userId && it.name.contains(query, ignoreCase = true) })
        }

        override fun insertMedication(medication: Medication): Completable {
            return Completable.fromAction { inMemoryMedications[medication.id] = medication }
        }

        override fun updateMedication(medication: Medication): Completable {
            return Completable.fromAction { inMemoryMedications[medication.id] = medication }
        }

        override fun deleteMedication(medication: Medication): Completable {
            return Completable.fromAction { inMemoryMedications.remove(medication.id) }
        }
    }

    private val backgroundScheduler = Schedulers.io()

    fun loadMedications(userId: String): Flowable<List<Medication>> {
        return medicationDao.getMedicationsForUser(userId).subscribeOn(backgroundScheduler)
    }

    fun loadMedicationDetails(medicationId: String): Flowable<Medication> {
        return medicationDao.getMedicationById(medicationId).subscribeOn(backgroundScheduler)
    }

    fun addMedication(medication: Medication): Completable {
        return medicationDao.insertMedication(medication).subscribeOn(backgroundScheduler)
    }

    fun updateMedication(medication: Medication): Completable {
        return medicationDao.updateMedication(medication).subscribeOn(backgroundScheduler)
    }

    fun deleteMedication(medication: Medication): Completable {
        return medicationDao.deleteMedication(medication).subscribeOn(backgroundScheduler)
    }

    fun searchMedications(userId: String, query: String): Flowable<List<Medication>> {
        return medicationDao.searchMedicationsForUser(userId, query).subscribeOn(backgroundScheduler)
    }
}
