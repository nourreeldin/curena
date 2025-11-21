package com.fueians.medicationapp.presenter.DrugInteraction

import com.fueians.medicationapp.model.repository.DrugInfo
import com.fueians.medicationapp.model.repository.DrugInfoRepository
import com.fueians.medicationapp.model.repository.InteractionDetail
import com.fueians.medicationapp.model.repository.InteractionResult
import com.fueians.medicationapp.model.repository.Medication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

// =========================================================================
// 1. View Interface
// =========================================================================

/**
 * Interface for the Drug Interaction View (e.g., Activity or Fragment).
 */
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
// 2. Drug Interaction Service Placeholder (Dependency)
// =========================================================================

/**
 * Placeholder service for the external interaction check logic.
 */
class DrugInteractionService(
    // Dependencies injected in real app
    private val drugAPIClient: DrugAPIClient
)

/**
 * Placeholder client for API interaction.
 */
class DrugAPIClient

// =========================================================================
// 3. Presenter
// =========================================================================

/**
 * Handles drug interaction checks, searching the drug database, and providing drug information.
 *
 * Usage: Used by drug search and interaction checker activities.
 */
class DrugInteractionPresenter(
    private var view: IDrugInteractionView?,
    private val drugInfoRepository: DrugInfoRepository,
    private val drugInteractionService: DrugInteractionService
) {
    // compositeDisposable replaced by CoroutineScope for modern Kotlin concurrency.
    private val presenterScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // --- Core Methods ---

    /**
     * Attach view when the Activity/Fragment is created.
     */
    fun attachView(view: IDrugInteractionView) {
        this.view = view
    }

    /**
     * Detach view when the Activity/Fragment is destroyed to prevent memory leaks.
     */
    fun detachView() {
        this.view = null
        presenterScope.cancel() // Cancel all coroutines launched in this scope
    }

    // --- Search and Load ---

    /**
     * Search the drug database based on a text query.
     */
    fun searchDrug(query: String) {
        presenterScope.launch {
            drugInfoRepository.searchDrug(query)
                .onStart { view?.showLoading() }
                .onCompletion { view?.hideLoading() }
                .catch { exception ->
                    view?.showErrorMessage("Search failed: ${exception.message}")
                }
                .collect { drugs ->
                    view?.displaySearchResults(drugs)
                }
        }
    }

    /**
     * Load detailed information for a specific drug.
     */
    fun loadDrugInfo(drugId: String) {
        presenterScope.launch {
            drugInfoRepository.loadDrugInfo(drugId)
                .onStart { view?.showLoading() }
                .onCompletion { view?.hideLoading() }
                .catch { exception ->
                    view?.showErrorMessage("Failed to load drug info: ${exception.message}")
                }
                .collect { drug ->
                    if (drug != null) {
                        view?.displayDrugInfo(drug)
                    } else {
                        view?.showErrorMessage("Drug information not found.")
                    }
                }
        }
    }

    // --- Interaction Check ---

    /**
     * Check interactions between a list of medications.
     */
    fun checkInteractions(medications: List<Medication>) {
        presenterScope.launch {
            view?.showLoading()
            try {
                if (medications.size < 2) {
                    throw IllegalStateException("Requires at least two medications for an interaction check.")
                }
                val result = drugInfoRepository.checkInteractions(medications)
                view?.displayInteractionResults(result)
            } catch (e: Exception) {
                view?.showErrorMessage("Error checking interactions: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }

    /**
     * Load detailed information for a specific interaction.
     */
    fun loadInteractionDetails(interactionId: String) {
        presenterScope.launch {
            drugInfoRepository.loadInteractionDetails(interactionId)
                .onStart { view?.showLoading() }
                .onCompletion { view?.hideLoading() }
                .catch { exception ->
                    view?.showErrorMessage("Failed to load interaction details: ${exception.message}")
                }
                .collect { details ->
                    view?.displayInteractionDetails(details)
                }
        }
    }

    // --- Persistence ---

    /**
     * Cache drug information locally.
     */
    fun saveDrugInfo(drugInfo: DrugInfo) {
        presenterScope.launch {
            view?.showLoading()
            try {
                drugInfoRepository.saveDrugInfo(drugInfo)
                view?.onDrugInfoSaved()
            } catch (e: Exception) {
                view?.showErrorMessage("Error saving drug info: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }
}