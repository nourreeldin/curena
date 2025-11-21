package com.fueians.medicationapp.model.repository

import io.reactivex.Completable
import io.reactivex.Observable
import com.fueians.medicationapp.model.dao.CaregiverPatientDao
import com.fueians.medicationapp.model.dao.UserDao
import com.fueians.medicationapp.model.remote.SupabaseClient

// CaregiverRepository: Handles caregiver-patient relationships and patient data access
class CaregiverRepository(
    private val caregiverPatientDao: CaregiverPatientDao,
    private val userDao: UserDao,
    private val supabaseClient: SupabaseClient
) {


    // Get all patients linked to caregiver
    fun getPatients(): Observable<List<User>> {
        return caregiverPatientDao.getPatients()
    }


    // Get patient details by ID
    fun getPatientById(id: String): Observable<User> {
        return userDao.getUserById(id)
    }


    // Add patient using email (remote + save locally)
    fun addPatient(patientEmail: String): Completable {
        return supabaseClient.addPatient(patientEmail)
            .doOnComplete { caregiverPatientDao.addPatient(patientEmail) }
    }


    // Remove patient from caregiver list
    fun removePatient(patientId: String): Completable {
        return Completable.fromAction {
            caregiverPatientDao.removePatient(patientId)
        }.andThen(supabaseClient.removePatient(patientId))
    }


    // Send invitation to patient email
    fun sendInvitation(email: String): Completable {
        return supabaseClient.sendInvitation(email)
    }


    // Get patient adherence percentage
    fun getPatientAdherence(patientId: String): Observable<Float> {
        return supabaseClient.getPatientAdherence(patientId)
    }


    // Get medications assigned to a patient
    fun getPatientMedications(patientId: String): Observable<List<Medication>> {
        return supabaseClient.getPatientMedications(patientId)
    }
}