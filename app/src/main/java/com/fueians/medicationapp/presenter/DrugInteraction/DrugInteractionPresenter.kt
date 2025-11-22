package com.fueians.medicationapp.presenter.DrugInteraction

import com.fueians.medicationapp.model.entities.DrugInfoEntity
import com.fueians.medicationapp.model.repository.DrugInfoRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

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
    fun displaySearchResults(drugs: List<DrugInfoEntity>)
    fun displayDrugInfo(drug: DrugInfoEntity)
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
// 4. Presenter (Updated for RxJava)
// =========================================================================
class DrugInteractionPresenter(private var view: IDrugInteractionView?, private val drugInfoRepository: DrugInfoRepository) {

    private val compositeDisposable = CompositeDisposable()
    private val drugInteractionService = DrugInteractionService()

    fun attachView(view: IDrugInteractionView) {
        this.view = view
    }

    fun detachView() {
        view = null
        compositeDisposable.clear()
    }

    fun searchDrug(query: String) {
        view?.showLoading()
        val disposable = drugInfoRepository.searchDrugs(query)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.displaySearchResults(it)
            }, {
                view?.hideLoading()
                view?.showErrorMessage("Search failed: ${it.message}")
            })
        compositeDisposable.add(disposable)
    }

    fun loadDrugInfo(drugId: String) {
        view?.showLoading()
        val disposable = drugInfoRepository.getDrugById(drugId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.displayDrugInfo(it)
            }, {
                view?.hideLoading()
                view?.showErrorMessage("Failed to load drug info: ${it.message}")
            })
        compositeDisposable.add(disposable)
    }

    fun checkInteractions(medications: List<Medication>) {
        // This service is synchronous, so no RxJava needed unless it were a network call
        view?.showLoading()
        try {
            val result = drugInteractionService.checkInteractions(medications)
            view?.hideLoading()
            view?.displayInteractionResults(result)
        } catch (e: Exception) {
            view?.hideLoading()
            view?.showErrorMessage("Error checking interactions: ${e.message}")
        }
    }

    fun loadInteractionDetails(interactionId: String) {
        // This service is synchronous
        view?.showLoading()
        try {
            val details = drugInteractionService.loadInteractionDetails(interactionId)
            view?.hideLoading()
            view?.displayInteractionDetails(details)
        } catch (e: Exception) {
            view?.hideLoading()
            view?.showErrorMessage("Failed to load interaction details: ${e.message}")
        }
    }

    fun saveDrugInfo(drugInfo: DrugInfoEntity) {
        view?.showLoading()
        val disposable = drugInfoRepository.insertDrugInfo(drugInfo)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.onDrugInfoSaved()
            }, {
                view?.hideLoading()
                view?.showErrorMessage("Error saving drug info: ${it.message}")
            })
        compositeDisposable.add(disposable)
    }
}
