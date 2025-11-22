package com.fueians.medicationapp.presenter.Medication

import com.fueians.medicationapp.model.entities.Medication
import com.fueians.medicationapp.model.repository.MedicationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Placeholder service - this would have its own file in a real app
class DrugInteractionService {
    fun checkInteractions(medications: List<Medication>): List<String> {
        println("Checking drug interactions...")
        return if (medications.size > 1) listOf("Potential interaction detected.") else emptyList()
    }
}

// View interface - this should be in its own file
interface IMedicationView {
    fun showLoading()
    fun hideLoading()
    fun displayMedications(medications: List<Medication>)
    fun displayMedicationDetails(medication: Medication)
    fun displayError(message: String)
    fun onMedicationAdded()
    fun onMedicationUpdated()
    fun onMedicationDeleted()
    fun displayInteractions(interactions: List<String>)
}

class MedicationPresenter(private var view: IMedicationView?) {

    // Dependencies are now private attributes, instantiated by the presenter.
    private val medicationRepository = MedicationRepository()
    private val drugInteractionService = DrugInteractionService()

    private val presenterScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun attachView(view: IMedicationView) {
        this.view = view
    }

    fun detachView() {
        view = null
        presenterScope.cancel()
    }

    fun loadMedications(userId: String) {
        view?.showLoading()
        presenterScope.launch {
            try {
                val medications = withContext(Dispatchers.IO) {
                    medicationRepository.loadMedications(userId)
                }
                view?.hideLoading()
                view?.displayMedications(medications)
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to load medications.")
            }
        }
    }

    fun addMedication(medication: Medication) {
        view?.showLoading()
        presenterScope.launch {
            try {
                withContext(Dispatchers.IO) { medicationRepository.addMedication(medication) }
                view?.hideLoading()
                view?.onMedicationAdded()
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to add medication.")
            }
        }
    }

    fun updateMedication(medication: Medication) {
        view?.showLoading()
        presenterScope.launch {
            try {
                withContext(Dispatchers.IO) { medicationRepository.updateMedication(medication) }
                view?.hideLoading()
                view?.onMedicationUpdated()
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to update medication.")
            }
        }
    }

    fun deleteMedication(medication: Medication) {
        view?.showLoading()
        presenterScope.launch {
            try {
                withContext(Dispatchers.IO) { medicationRepository.deleteMedication(medication) }
                view?.hideLoading()
                view?.onMedicationDeleted()
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to delete medication.")
            }
        }
    }

    fun searchMedications(userId: String, query: String) {
        view?.showLoading()
        presenterScope.launch {
            try {
                val medications = withContext(Dispatchers.IO) {
                    medicationRepository.searchMedications(userId, query)
                }
                view?.hideLoading()
                view?.displayMedications(medications)
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to search medications.")
            }
        }
    }

    fun checkDrugInteractions(medications: List<Medication>) {
        presenterScope.launch {
            // This service is synchronous and doesn't need a background thread
            val interactions = drugInteractionService.checkInteractions(medications)
            view?.displayInteractions(interactions)
        }
    }
}
