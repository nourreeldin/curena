package com.fueians.medicationapp.model.repository

import android.content.Context
import com.fueians.medicationapp.model.dao.MedicationDao
import com.fueians.medicationapp.model.dao.AdherenceLogDao
import com.fueians.medicationapp.model.dao.RefillDao
import com.fueians.medicationapp.model.entities.MedicationEntity
import com.fueians.medicationapp.model.entities.RefillEntity
import com.fueians.medicationapp.model.database.AppDatabase
import com.fueians.medicationapp.model.services.NotificationService
import com.fueians.medicationapp.model.services.ReminderService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MedicationRepository(context: Context) {

    private val medicationDao: MedicationDao by lazy {
        AppDatabase.getInstance(context).medicationDao()
    }
    private val adherenceLogDao: AdherenceLogDao by lazy {
        AppDatabase.getInstance(context).adherenceLogDao()
    }
    private val refillDao: RefillDao by lazy {
        AppDatabase.getInstance(context).refillDao()
    }
    private val notificationService: NotificationService by lazy {
        NotificationService(context)
    }
    private val reminderService: ReminderService by lazy {
        ReminderService(context)
    }

    /**
     * Get all medications as Flow
     */
    fun getAllMedications(userId: String): Flow<List<MedicationEntity>> {
        return medicationDao.getAllMedications(userId)
    }

    /**
     * Get medication by ID
     */
    suspend fun getMedicationById(id: String): MedicationEntity? = withContext(Dispatchers.IO) {
        medicationDao.getMedicationById(id)
    }

    /**
     * Get active medications
     */
    fun getActiveMedications(userId: String): Flow<List<MedicationEntity>> {
        return medicationDao.getActiveMedications(userId)
    }

    /**
     * Add medication
     */
    suspend fun addMedication(medication: MedicationEntity) = withContext(Dispatchers.IO) {
        medicationDao.insertMedication(medication)
    }

    /**
     * Update medication
     */
    suspend fun updateMedication(medication: MedicationEntity) = withContext(Dispatchers.IO) {
        medicationDao.updateMedication(medication)
    }

    /**
     * Delete medication
     */
    suspend fun deleteMedication(id: String) = withContext(Dispatchers.IO) {
        val medication = medicationDao.getMedicationById(id)
        if (medication != null) {
            medicationDao.deleteMedication(medication)
        }
    }

    /**
     * Search medications
     */
    fun searchMedications(userId: String, query: String): Flow<List<MedicationEntity>> {
        return medicationDao.searchMedications(userId, query)
    }

    /**
     * Get refill for medication
     */
    suspend fun getRefillForMedication(medicationId: String): RefillEntity? =
        withContext(Dispatchers.IO) {
            refillDao.getRefillByMedicationId(medicationId)
        }

    /**
     * Update refill
     */
    suspend fun updateRefill(refill: RefillEntity) = withContext(Dispatchers.IO) {
        refillDao.updateRefill(refill)

        // Send notification if refill is due
        if (refill.shouldSendReminder()) {
            notificationService.sendRefillReminder(refill)
        }
    }

    /**
     * Get medications needing refill
     */
    suspend fun getMedicationsNeedingRefill(): List<RefillEntity> = withContext(Dispatchers.IO) {
        refillDao.getRefillsDueSoon()
    }
}
