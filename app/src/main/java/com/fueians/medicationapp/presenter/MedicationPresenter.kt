package com.fueians.medicationapp.presenter.Medication

import android.content.Context
import com.fueians.medicationapp.model.entities.MedicationEntity
import com.fueians.medicationapp.model.repository.MedicationRepository
import com.fueians.medicationapp.view.interfaces.IMedicationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


// View interface - this should be in its own file

class MedicationPresenter(private var view: IMedicationView?, context: Context) {
    private val medicationRepository = MedicationRepository(context)
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
                medicationRepository.getAllMedications(userId).collectLatest { meds ->
                    view?.hideLoading()
                    view?.displayMedications(meds)
                }
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to load medications")
            }
        }
    }

    fun addMedication(med: MedicationEntity) {
        view?.showLoading()
        presenterScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    medicationRepository.addMedication(med)
                }
                view?.hideLoading()
                view?.onMedicationAdded()
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to add medication")
            }
        }
    }

    fun updateMedication(med: MedicationEntity) {
        view?.showLoading()
        presenterScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    medicationRepository.updateMedication(med)
                }
                view?.hideLoading()
                view?.onMedicationUpdated()
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to update medication")
            }
        }
    }

    fun deleteMedication(med: MedicationEntity) {
        view?.showLoading()
        presenterScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    medicationRepository.deleteMedication(med.id)
                }
                view?.hideLoading()
                view?.onMedicationDeleted()
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to delete medication")
            }
        }
    }

    fun searchMedications(userId: String, query: String) {
        view?.showLoading()
        presenterScope.launch {
            try {
                medicationRepository.searchMedications(userId, query).collectLatest { meds ->
                    view?.hideLoading()
                    view?.displayMedications(meds)
                }
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to search medications")
            }
        }
    }
}
