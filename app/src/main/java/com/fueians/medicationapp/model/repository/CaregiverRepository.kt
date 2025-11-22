package com.fueians.medicationapp.model.repository

import com.fueians.medicationapp.model.dao.AdherenceLogDao
import com.fueians.medicationapp.model.dao.CaregiverPatientDao
import com.fueians.medicationapp.model.dao.MedicationDao
import com.fueians.medicationapp.model.dao.UserDao
import com.fueians.medicationapp.model.entities.AdherenceLog
import com.fueians.medicationapp.model.entities.CaregiverPatientEntity
import com.fueians.medicationapp.model.entities.Medication
import com.fueians.medicationapp.model.entities.UserEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * CaregiverRepository
 *
 * Responsibility: Provide a clean, RxJava-based API for caregiver-related data operations.
 */
class CaregiverRepository {

    // DAOs are now private attributes with placeholder implementations.
    private val caregiverPatientDao: CaregiverPatientDao = object : CaregiverPatientDao {
        private val relations = mutableListOf<CaregiverPatientEntity>()
        override fun getPatientRelationships(caregiverId: String): Flowable<List<CaregiverPatientEntity>> = Flowable.just(relations.filter { it.caregiverId == caregiverId })
        override fun addPatientRelationship(relation: CaregiverPatientEntity): Completable = Completable.fromAction { relations.add(relation) }
        override fun removePatientRelationship(patientId: String, caregiverId: String): Completable = Completable.fromAction { relations.removeIf { it.patientId == patientId && it.caregiverId == caregiverId } }
    }

    private val userDao: UserDao = object : UserDao {
        private val users = mutableMapOf<String, UserEntity>()
        override fun getUserById(id: String): Flowable<UserEntity> = Flowable.fromIterable(users.values.filter { it.id == id })
        override fun getUserByEmail(email: String): Single<UserEntity> {
            val user = users.values.find { it.email == email }
            return if (user != null) Single.just(user) else Single.error(androidx.room.EmptyResultSetException("Query returned no rows"))
        }
        override fun insertUser(user: UserEntity): Completable = Completable.fromAction { users[user.id] = user }
        override fun updateUser(user: UserEntity): Completable = Completable.fromAction { users[user.id] = user }
        override fun deleteUser(user: UserEntity): Completable = Completable.fromAction { users.remove(user.id) }
    }

    private val adherenceLogDao: AdherenceLogDao = object : AdherenceLogDao {
        override fun getLogsForPatient(patientId: String): Flowable<List<AdherenceLog>> = Flowable.just(emptyList())
        override fun insertLog(log: AdherenceLog): Completable = Completable.complete()
        override fun getMissedDoses(): Flowable<List<AdherenceLog>> = Flowable.just(emptyList())
    }

    private val medicationDao: MedicationDao = object : MedicationDao {
        override fun getMedicationsForUser(userId: String): Flowable<List<Medication>> = Flowable.just(emptyList())
        override fun getMedicationById(medicationId: String): Flowable<Medication> = Flowable.empty()
        override fun searchMedicationsForUser(userId: String, query: String): Flowable<List<Medication>> = Flowable.just(emptyList())
        override fun insertMedication(medication: Medication): Completable = Completable.complete()
        override fun updateMedication(medication: Medication): Completable = Completable.complete()
        override fun deleteMedication(medication: Medication): Completable = Completable.complete()
    }

    private val backgroundScheduler = Schedulers.io()

    fun loadPatients(caregiverId: String): Flowable<List<UserEntity>> {
        return caregiverPatientDao.getPatientRelationships(caregiverId)
            .flatMap { relations ->
                val patientFlowables = relations.map { relation -> userDao.getUserById(relation.patientId) }
                Flowable.combineLatest(patientFlowables) { users -> users.map { it } }
            }.subscribeOn(backgroundScheduler)
    }

    fun loadPatientDetails(patientId: String): Flowable<UserEntity> {
        return userDao.getUserById(patientId).subscribeOn(backgroundScheduler)
    }

    fun addPatient(caregiverId: String, patientEmail: String): Completable {
        return userDao.getUserByEmail(patientEmail)
            .flatMapCompletable { patient ->
                val relation = CaregiverPatientEntity(caregiverId, patient.id)
                caregiverPatientDao.addPatientRelationship(relation)
            }.subscribeOn(backgroundScheduler)
    }

    fun removePatient(patientId: String, caregiverId: String): Completable {
        return caregiverPatientDao.removePatientRelationship(patientId, caregiverId).subscribeOn(backgroundScheduler)
    }

    fun sendInvitation(email: String): Completable {
        return Completable.fromAction { println("Sending caregiver invitation to $email...") }.subscribeOn(backgroundScheduler)
    }

    fun loadPatientAdherence(patientId: String): Flowable<List<AdherenceLog>> {
        return adherenceLogDao.getLogsForPatient(patientId).subscribeOn(backgroundScheduler)
    }

    fun loadPatientMedications(patientId: String): Flowable<List<Medication>> {
        return medicationDao.getMedicationsForUser(patientId).subscribeOn(backgroundScheduler)
    }
}
