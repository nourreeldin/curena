package com.fueians.medicationapp.presenter.Medication

import com.fueians.medicationapp.model.entities.Medication
import com.fueians.medicationapp.model.repository.MedicationRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

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

    private val medicationRepository = MedicationRepository()
    private val drugInteractionService = DrugInteractionService()
    private val compositeDisposable = CompositeDisposable()

    fun attachView(view: IMedicationView) {
        this.view = view
    }

    fun detachView() {
        view = null
        compositeDisposable.clear()
    }

    fun loadMedications(userId: String) {
        view?.showLoading()
        val disposable = medicationRepository.loadMedications(userId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.displayMedications(it)
            }, {
                view?.hideLoading()
                view?.displayError(it.message ?: "Failed to load medications.")
            })
        compositeDisposable.add(disposable)
    }

    fun addMedication(medication: Medication) {
        view?.showLoading()
        val disposable = medicationRepository.addMedication(medication)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.onMedicationAdded()
            }, {
                view?.hideLoading()
                view?.displayError(it.message ?: "Failed to add medication.")
            })
        compositeDisposable.add(disposable)
    }

    fun updateMedication(medication: Medication) {
        view?.showLoading()
        val disposable = medicationRepository.updateMedication(medication)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.onMedicationUpdated()
            }, {
                view?.hideLoading()
                view?.displayError(it.message ?: "Failed to update medication.")
            })
        compositeDisposable.add(disposable)
    }

    fun deleteMedication(medication: Medication) {
        view?.showLoading()
        val disposable = medicationRepository.deleteMedication(medication)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.onMedicationDeleted()
            }, {
                view?.hideLoading()
                view?.displayError(it.message ?: "Failed to delete medication.")
            })
        compositeDisposable.add(disposable)
    }

    fun searchMedications(userId: String, query: String) {
        view?.showLoading()
        val disposable = medicationRepository.searchMedications(userId, query)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.displayMedications(it)
            }, {
                view?.hideLoading()
                view?.displayError(it.message ?: "Failed to search medications.")
            })
        compositeDisposable.add(disposable)
    }

    fun checkDrugInteractions(medications: List<Medication>) {
        // This service is synchronous, so no RxJava is needed
        val interactions = drugInteractionService.checkInteractions(medications)
        view?.displayInteractions(interactions)
    }
}
