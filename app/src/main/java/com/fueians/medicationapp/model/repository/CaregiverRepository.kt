package com.fueians.medicationapp.model.repository

import com.fueians.medicationapp.model.dao.AdherenceLogDao
import com.fueians.medicationapp.model.dao.CaregiverPatientDao
import com.fueians.medicationapp.model.dao.MedicationDao
import com.fueians.medicationapp.model.dao.UserDao
import com.fueians.medicationapp.model.entities.AdherenceLog
import com.fueians.medicationapp.model.entities.CaregiverPatientEntity
import com.fueians.medicationapp.model.entities.Medication
import com.fueians.medicationapp.model.entities.UserEntity

/**
 * CaregiverRepository
 *
 * Responsibility: Provide a clean, synchronous API for caregiver-related data operations.
 * All methods in this repository perform blocking I/O and MUST be called from a background thread.
 */
class CaregiverRepository {

    // DAOs are now private attributes with placeholder implementations.
    private val caregiverPatientDao: CaregiverPatientDao = object : CaregiverPatientDao {
        private val relations = mutableListOf<CaregiverPatientEntity>()
        override fun getPatientRelationships(caregiverId: String) = relations.filter { it.caregiverId == caregiverId }
        override fun addPatientRelationship(relation: CaregiverPatientEntity) { relations.add(relation) }
        override fun removePatientRelationship(patientId: String, caregiverId: String) {
            relations.removeIf { it.patientId == patientId && it.caregiverId == caregiverId }
        }
    }

    private val userDao: UserDao = object : UserDao {
        private val users = mutableMapOf<String, UserEntity>()
        override fun getUserById(id: String) = users[id]
        override fun getUserByEmail(email: String) = users.values.find { it.email == email }
        override fun insertUser(user: UserEntity) { users[user.id] = user }
        override fun updateUser(user: UserEntity) { users[user.id] = user }
        override fun deleteUser(user: UserEntity) { users.remove(user.id) }
    }

    private val adherenceLogDao: AdherenceLogDao = object : AdherenceLogDao {
        override fun getLogsForPatient(patientId: String): List<AdherenceLog> = emptyList()
        override fun insertLog(log: AdherenceLog) {}
        override fun getMissedDoses(): List<AdherenceLog> = emptyList()
    }

    private val medicationDao: MedicationDao = object : MedicationDao {
        override fun getMedicationsForUser(userId: String): List<Medication> = emptyList()
        override fun getMedicationById(medicationId: String): Medication? = null
        override fun searchMedicationsForUser(userId: String, query: String): List<Medication> = emptyList()
        override fun insertMedication(medication: Medication) {}
        override fun updateMedication(medication: Medication) {}
        override fun deleteMedication(medication: Medication) {}
    }

    fun loadPatients(caregiverId: String): List<UserEntity> {
        val patientRelations = caregiverPatientDao.getPatientRelationships(caregiverId)
        return patientRelations.mapNotNull { relation ->
            userDao.getUserById(relation.patientId)
        }
    }

    fun loadPatientDetails(patientId: String): UserEntity? {
        return userDao.getUserById(patientId)
    }

    fun addPatient(caregiverId: String, patientEmail: String) {
        val patient = userDao.getUserByEmail(patientEmail)
            ?: throw Exception("Patient with email $patientEmail not found.")
        val relation = CaregiverPatientEntity(caregiverId, patient.id)
        caregiverPatientDao.addPatientRelationship(relation)
    }

    fun removePatient(patientId: String, caregiverId: String) {
        caregiverPatientDao.removePatientRelationship(patientId, caregiverId)
    }

    fun sendInvitation(email: String) {
        println("Sending caregiver invitation to $email...")
    }

    fun loadPatientAdherence(patientId: String): List<AdherenceLog> {
        return adherenceLogDao.getLogsForPatient(patientId)
    }

    fun loadPatientMedications(patientId: String): List<Medication> {
        return medicationDao.getMedicationsForUser(patientId)
    }
}
