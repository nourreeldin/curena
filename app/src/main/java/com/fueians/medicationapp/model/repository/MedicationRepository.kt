package com.fueians.medicationapp.model.repository

import com.fueians.medicationapp.model.dao.MedicationDao
import com.fueians.medicationapp.model.entities.Medication

/**
 * MedicationRepository
 *
 * Responsibility: Provide a clean, synchronous API for medication data operations.
 * All methods in this repository perform blocking I/O and MUST be called from a background thread.
 */
class MedicationRepository {

    // DAO is now a private attribute with a placeholder implementation.
    private val medicationDao: MedicationDao = object : MedicationDao {
        private val inMemoryMedications = mutableMapOf<String, Medication>()

        override fun getMedicationsForUser(userId: String): List<Medication> {
            return inMemoryMedications.values.filter { it.userId == userId }
        }

        override fun getMedicationById(medicationId: String): Medication? {
            return inMemoryMedications[medicationId]
        }

        override fun searchMedicationsForUser(userId: String, query: String): List<Medication> {
            return inMemoryMedications.values.filter { it.userId == userId && it.name.contains(query, ignoreCase = true) }
        }

        override fun insertMedication(medication: Medication) {
            inMemoryMedications[medication.id] = medication
        }

        override fun updateMedication(medication: Medication) {
            inMemoryMedications[medication.id] = medication
        }

        override fun deleteMedication(medication: Medication) {
            inMemoryMedications.remove(medication.id)
        }
    }

    fun loadMedications(userId: String): List<Medication> {
        return medicationDao.getMedicationsForUser(userId)
    }

    fun loadMedicationDetails(medicationId: String): Medication? {
        return medicationDao.getMedicationById(medicationId)
    }

    fun addMedication(medication: Medication) {
        medicationDao.insertMedication(medication)
    }

    fun updateMedication(medication: Medication) {
        medicationDao.updateMedication(medication)
    }

    fun deleteMedication(medication: Medication) {
        medicationDao.deleteMedication(medication)
    }

    fun searchMedications(userId: String, query: String): List<Medication> {
        return medicationDao.searchMedicationsForUser(userId, query)
    }
}
