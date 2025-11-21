package com.fueians.medicationapp.model.repository

import io.reactivex.Completable
import io.reactivex.Observable

import com.fueians.medicationapp.model.dao.MedicationDao
import com.fueians.medicationapp.model.dao.AdherenceLogDao
import com.fueians.medicationapp.model.dao.RefillDao
import com.fueians.medicationapp.remote.SupabaseClient
import com.fueians.medicationapp.model.services.NotificationService
import com.fueians.medicationapp.model.services.SyncService
import com.fueians.medicationapp.model.services.ReminderService


class MedicationRepository (
    private val medicationDao: MedicationDao,
    private val adherenceLogDao: AdherenceLogDao,
    private val refillDao: RefillDao,
    private val supabaseClient: SupabaseClient,
    private val notificationService: NotificationService,
    private val reminderService: ReminderService,
    private val syncService: SyncService
){
    // ---------------------------------------------------------
    // GET ALL MEDICATIONS
    // ---------------------------------------------------------
    fun getAllMedications(): Observable<List<Medication>> {
        return medicationDao.getAllMedications()
    }

    // ---------------------------------------------------------
    // ADD MEDICATION
    // ---------------------------------------------------------
    fun addMedication(medication: Medication): Completable {
        return Completable.fromAction {
            medicationDao.insertMedication(medication)

            reminderService.scheduleMedicationReminder(medication)

            notificationService.sendCreatedMedicationNotification(medication)
        }
    }
    // ---------------------------------------------------------
    // UPDATE MEDICATION
    // ---------------------------------------------------------
    fun updateMedication(medication: Medication): Completable {
        return Completable.fromAction {
            medicationDao.updateMedication(medication)

            reminderService.updateMedicationReminder(medication)
        }
    }
    // ---------------------------------------------------------
    //  DELETE MEDICATION
    // ---------------------------------------------------------
    fun deleteMedication(id: String): Completable {
        return Completable.fromAction {
            val medication = medicationDao.getMedicationByIdSync(id)
            medicationDao.deleteMedication(id)

            reminderService.cancelMedicationReminder(id)

            notificationService.sendDeletedMedicationNotification(medication)
        }
    }

    // ---------------------------------------------------------
    //  SEARCH
    // ---------------------------------------------------------
    fun searchMedications(query: String): Observable<List<Medication>> {
        return medicationDao.searchMedications(query)
    }

    // ---------------------------------------------------------
    //  REFILL STATUS
    // ---------------------------------------------------------
    fun getRefillStatus(medicationId: String): Observable<Refill> {
        return refillDao.getRefillByMedicationId(medicationId)
    }

    fun updateRefill(refill: Refill): Completable {
        return Completable.fromAction {
            refillDao.updateRefill(refill)
        }
    }

    // ---------------------------------------------------------
    //  SYNC WITH SERVER
    // ---------------------------------------------------------
    fun syncMedications(): Completable {
        return Completable.fromAction {
            syncService.syncMedications()
        }
    }


}