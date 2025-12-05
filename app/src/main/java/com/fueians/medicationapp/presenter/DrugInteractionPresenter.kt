package com.fueians.medicationapp.presenter.DrugInteraction

import android.content.Context
import com.fueians.medicationapp.model.clients.DrugInfo
import com.fueians.medicationapp.model.repository.DrugInfoRepository
import com.fueians.medicationapp.view.interfaces.IDrugInteractionView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// =========================================================================
// 1. Presenter
// =========================================================================
class DrugInteractionPresenter(
    private var view: IDrugInteractionView?,
    context: Context,
    private val drugAPIClient: com.fueians.medicationapp.model.clients.DrugAPIClient
) {

    private val drugInfoRepository = DrugInfoRepository(context, drugAPIClient)
    private val presenterScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun attachView(view: IDrugInteractionView) {
        this.view = view
    }

    fun detachView() {
        view = null
        presenterScope.cancel()
    }

    // Search for drugs (uses API and local cache)
    fun searchDrug(query: String) {
        view?.showLoading()
        presenterScope.launch {
            try {
                val drugs = withContext(Dispatchers.IO) {
                    drugInfoRepository.searchDrug(query).blockingGet()
                }
                view?.hideLoading()
                view?.displaySearchResults(drugs) // error
            } catch (e: Exception) {
                view?.hideLoading()
                view?.showErrorMessage("Search failed: ${e.message}")
            }
        }
    }

    // Load detailed drug info by ID
    fun loadDrugInfo(drugId: String) {
        view?.showLoading()
        presenterScope.launch {
            try {
                val drug: DrugInfo? = withContext(Dispatchers.IO) {
                    drugInfoRepository.getDrugById(drugId).blockingGet()
                }
                view?.hideLoading()
                if (drug != null) {
                    view?.displayDrugInfo(drug) // error
                } else {
                    view?.showErrorMessage("Drug information not found.")
                }
            } catch (e: Exception) {
                view?.hideLoading()
                view?.showErrorMessage("Failed to load drug info: ${e.message}")
            }
        }
    }

    // Check interactions for a list of drug IDs
    fun checkInteractions(drugIds: List<String>) {
        if (drugIds.size < 2) {
            view?.showErrorMessage("At least two drugs are required for interaction check.")
            return
        }

        view?.showLoading()
        presenterScope.launch {
            try {
                val interactions: List<com.fueians.medicationapp.model.entities.DrugInteractionEntity> =
                    withContext(Dispatchers.IO) {
                        drugInfoRepository.checkInteractions(drugIds).blockingGet()
                            .map { interaction ->
                                drugInfoRepository.mapInteractionToEntity(interaction)
                            }
                    }
                view?.hideLoading()
                view?.displayInteractionResults(interactions)
            } catch (e: Exception) {
                view?.hideLoading()
                view?.showErrorMessage("Error checking interactions: ${e.message}")
            }
        }
    }

    // Get detailed interaction info for a specific interaction (from cache/DB)
    suspend fun loadInteractionDetails(interactionId: String) {
        view?.showLoading()
        presenterScope.launch {
            try {
                val interaction = withContext(Dispatchers.IO) {
                    drugInfoRepository.getInteraction(interactionId.split("_")[0], interactionId.split("_")[1])
                }
                view?.hideLoading()
                if (interaction != null) {
                    view?.displayInteractionDetails(interaction)
                } else {
                    view?.showErrorMessage("Interaction details not found.")
                }
            } catch (e: Exception) {
                view?.hideLoading()
                view?.showErrorMessage("Failed to load interaction details: ${e.message}")
            }
        }
    }

    // Save drug info to local cache (Room)
    fun saveDrugInfo(drugInfoEntity: com.fueians.medicationapp.model.entities.DrugInfoEntity) {
        view?.showLoading()
        presenterScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    drugInfoRepository.saveDrugInfo(drugInfoEntity)
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
