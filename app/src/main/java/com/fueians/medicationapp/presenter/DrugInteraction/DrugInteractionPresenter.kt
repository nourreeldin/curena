package com.fueians.medicationapp.presenter.DrugInteraction

import com.fueians.medicationapp.model.entities.DrugInfo
import com.fueians.medicationapp.model.repository.DrugInfoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// =========================================================================
// 1. Placeholder Models (for demonstration)
// =========================================================================
data class Medication(val id: String, val name: String)
data class InteractionResult(val id: String, val summary: String)
data class InteractionDetail(val id: String, val description: String)

// =========================================================================
// 2. View Interface
// =========================================================================
interface IDrugInteractionView {
    fun showLoading()
    fun hideLoading()
    fun displaySearchResults(drugs: List<DrugInfo>)
    fun displayDrugInfo(drug: DrugInfo)
    fun displayInteractionResults(result: InteractionResult)
    fun displayInteractionDetails(details: InteractionDetail)
    fun onDrugInfoSaved()
    fun showErrorMessage(message: String)
}

// =========================================================================
// 3. Drug Interaction Service Placeholder
// =========================================================================
class DrugInteractionService {
    fun checkInteractions(medications: List<Medication>): InteractionResult {
        println("Checking interactions for: ${medications.joinToString { it.name }}")
        if (medications.size < 2) {
            throw IllegalStateException("Requires at least two medications for an interaction check.")
        }
        return InteractionResult("interaction-123", "Minor interaction found between ${medications[0].name} and ${medications[1].name}.")
    }

    fun loadInteractionDetails(interactionId: String): InteractionDetail {
        println("Loading details for interaction: $interactionId")
        return InteractionDetail(interactionId, "Detailed description of the interaction and recommended actions.")
    }
}

// =========================================================================
// 4. Presenter
// =========================================================================
class DrugInteractionPresenter(private var view: IDrugInteractionView?) {

    // Dependencies are instantiated as private attributes.
    private val drugInfoRepository = DrugInfoRepository()
    private val drugInteractionService = DrugInteractionService()

    private val presenterScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun attachView(view: IDrugInteractionView) {
        this.view = view
    }

    fun detachView() {
        view = null
        presenterScope.cancel()
    }

    fun searchDrug(query: String) {
        view?.showLoading()
        presenterScope.launch {
            try {
                val drugs = withContext(Dispatchers.IO) {
                    drugInfoRepository.searchDrugs(query)
                }
                view?.hideLoading()
                view?.displaySearchResults(drugs)
            } catch (e: Exception) {
                view?.hideLoading()
                view?.showErrorMessage("Search failed: ${e.message}")
            }
        }
    }

    fun loadDrugInfo(drugId: String) {
        view?.showLoading()
        presenterScope.launch {
            try {
                val drug = withContext(Dispatchers.IO) {
                    drugInfoRepository.loadDrugInfo(drugId)
                }
                view?.hideLoading()
                if (drug != null) {
                    view?.displayDrugInfo(drug)
                } else {
                    view?.showErrorMessage("Drug information not found.")
                }
            } catch (e: Exception) {
                view?.hideLoading()
                view?.showErrorMessage("Failed to load drug info: ${e.message}")
            }
        }
    }

    fun checkInteractions(medications: List<Medication>) {
        view?.showLoading()
        presenterScope.launch {
            try {
                val result = withContext(Dispatchers.IO) { // Assumes this might be a network call in future
                    drugInteractionService.checkInteractions(medications)
                }
                view?.hideLoading()
                view?.displayInteractionResults(result)
            } catch (e: Exception) {
                view?.hideLoading()
                view?.showErrorMessage("Error checking interactions: ${e.message}")
            }
        }
    }

    fun loadInteractionDetails(interactionId: String) {
        view?.showLoading()
        presenterScope.launch {
            try {
                val details = withContext(Dispatchers.IO) { // Assumes this might be a network call
                    drugInteractionService.loadInteractionDetails(interactionId)
                }
                view?.hideLoading()
                view?.displayInteractionDetails(details)
            } catch (e: Exception) {
                view?.hideLoading()
                view?.showErrorMessage("Failed to load interaction details: ${e.message}")
            }
        }
    }

    fun saveDrugInfo(drugInfo: DrugInfo) {
        view?.showLoading()
        presenterScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    drugInfoRepository.saveDrugInfo(drugInfo)
                }
                view?.hideLoading()
                view?.onDrugInfoSaved()
            } catch (e: Exception) {
                view?.hideLoading()
                view?.showErrorMessage("Error saving drug info: ${e.message}")
            }
        }
    }
}
