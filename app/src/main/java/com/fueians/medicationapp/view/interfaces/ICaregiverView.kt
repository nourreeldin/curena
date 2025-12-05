package com.fueians.medicationapp.view.interfaces

import com.fueians.medicationapp.model.entities.CaregiverPatientEntity
import com.fueians.medicationapp.model.entities.MedicationEntity
import com.fueians.medicationapp.model.entities.UserEntity

interface ICaregiverView {
    fun showLoading()
    fun hideLoading()
    fun displayError(message: String)

    // Patients
    fun displayPatients(patients: List<CaregiverPatientEntity>)
    fun displayPatientDetails(patient: UserEntity)
    fun onPatientAdded()
    fun onPatientRemoved()

    // Medications
    fun displayPatientMedications(medications: List<MedicationEntity>)

    // Relationships
    fun displayActiveRelationships(relationships: List<CaregiverPatientEntity>)
    fun displayPendingInvitations(invitations: List<CaregiverPatientEntity>)
}
