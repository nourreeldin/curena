package com.fueians.medicationapp.presenter.Medication

import com.fueians.medicationapp.presenter.TestRepo.Medication
import com.fueians.medicationapp.presenter.TestRepo.MedicationRepository
import com.fueians.medicationapp.presenter.TestRepo.Refill
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// =========================================================================
// 1. Placeholder Service & View Interface
// =========================================================================

class DrugInteractionService {
    suspend fun checkInteractions(medications: List<Medication>): List<String> {
        // Placeholder logic
        return if (medications.size > 1) listOf("Potential interaction detected.") else emptyList()
    }
}

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

// =========================================================================
// 2. Presenter
// =========================================================================

class MedicationPresenter(
    private var view: IMedicationView?,
    // In a real app, these would be injected.
    private val medicationRepository: MedicationRepository,
    private val drugInteractionService: DrugInteractionService = DrugInteractionService()
) {

    private val presenterScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun attachView(view: IMedicationView) {
        this.view = view
    }

    fun detachView() {
        view = null
        presenterScope.cancel()
    }

    fun loadMedications() {
        view?.showLoading()
        presenterScope.launch {
            try {
                medicationRepository.loadMedications().collectLatest {
                    view?.displayMedications(it)
                    view?.hideLoading()
                }
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to load medications.")
            }
        }
    }

    fun loadMedicationDetails(medicationId: String) {
        view?.showLoading()
        presenterScope.launch {
            try {
                medicationRepository.loadMedicationDetails(medicationId).collectLatest {
                    it?.let { view?.displayMedicationDetails(it) }
                    view?.hideLoading()
                }
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to load medication details.")
            }
        }
    }

    fun addMedication(medication: Medication) {
        view?.showLoading()
        presenterScope.launch {
            try {
                medicationRepository.addMedication(medication)
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
                medicationRepository.updateMedication(medication)
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
                medicationRepository.deleteMedication(medication)
                view?.hideLoading()
                view?.onMedicationDeleted()
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to delete medication.")
            }
        }
    }

    fun searchMedications(query: String) {
        view?.showLoading()
        presenterScope.launch {
            try {
                medicationRepository.searchMedications(query).collectLatest {
                    view?.displayMedications(it)
                    view?.hideLoading()
                }
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to search medications.")
            }
        }
    }

    fun checkDrugInteractions(medications: List<Medication>) {
        presenterScope.launch {
            try {
                val interactions = drugInteractionService.checkInteractions(medications)
                view?.displayInteractions(interactions)
            } catch (e: Exception) {
                view?.displayError(e.message ?: "Failed to check interactions.")
            }
        }
    }

    fun updateRefillStatus(refill: Refill) {
        presenterScope.launch {
            try {
                medicationRepository.updateRefillStatus(refill)
            } catch (e: Exception) {
                view?.displayError(e.message ?: "Failed to update refill status.")
            }
        }
    }
}
