package com.fueians.medicationapp.view.interfaces

import com.fueians.medicationapp.model.entities.MedicationEntity

interface IMedicationView {
    fun showLoading()
    fun hideLoading()
    fun displayMedications(medications: List<MedicationEntity>)
    fun displayMedicationDetails(medication: MedicationEntity)
    fun displayError(message: String)
    fun onMedicationAdded()
    fun onMedicationUpdated()
    fun onMedicationDeleted()
    fun displayInteractions(interactions: List<String>)
}