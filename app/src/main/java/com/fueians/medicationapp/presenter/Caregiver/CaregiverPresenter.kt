package com.fueians.medicationapp.presenter.Caregiver

import com.fueians.medicationapp.model.entities.CaregiverPatientEntity
import com.fueians.medicationapp.model.entities.UserEntity
import com.fueians.medicationapp.presenter.Caregiver.CaregiverRepository
import com.fueians.medicationapp.presenter.Caregiver.MedicationRepository // Renamed for structure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

// =========================================================================
// 1. View Interface
// =========================================================================

/**
 * Interface for the Caregiver View (e.g., Activity or Fragment).
 */
interface ICaregiverView {
    fun showLoading()
    fun hideLoading()
    fun showPatients(patients: List<CaregiverPatientEntity>)
    fun showPatientDetails(patient: UserEntity)
    fun onPatientAdded(patientEmail: String)
    fun onPatientRemoved(patientId: String)
    fun onInvitationSent(email: String)
    fun showAdherenceData(data: List<AdherenceLog>)
    fun showMedications(medications: List<Medication>)
    fun showErrorMessage(message: String)
}

// =========================================================================
// 2. Presenter
// =========================================================================

class CaregiverPresenter(
    private var view: ICaregiverView?,
    private val caregiverRepository: CaregiverRepository,
    private val medicationRepository: MedicationRepository // Assuming a basic repository exists
) {

    // Used for coroutines launched by this Presenter.
    private val presenterScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // --- Attributes ---
    // view: ICaregiverView - Attached view (in constructor)
    // caregiverRepository: CaregiverRepository - Caregiver data repository (in constructor)
    // medicationRepository: MedicationRepository - Medication data repository (in constructor)

    // compositeDisposable is not needed when using Coroutines/Flows.

    /**
     * Attach view when the Activity/Fragment is created.
     */
    fun attachView(view: ICaregiverView) {
        this.view = view
    }

    /**
     * Detach view when the Activity/Fragment is destroyed to prevent memory leaks.
     */
    fun detachView() {
        this.view = null
        presenterScope.cancel() // Cancel all coroutines launched in this scope
    }

    /**
     * Load all patients related to the current caregiver.
     * Assumes the caregiver's ID is managed internally or injected.
     */
    fun loadPatients(caregiverId: String) {
        presenterScope.launch {
            caregiverRepository.loadPatients(caregiverId)
                .onStart { view?.showLoading() }
                .onCompletion { view?.hideLoading() }
                .catch { exception ->
                    view?.showErrorMessage("Failed to load patients: ${exception.message}")
                }
                .collect { patients ->
                    view?.showPatients(patients)
                }
        }
    }

    /**
     * Load detailed information for a specific patient.
     */
    fun loadPatientDetails(patientId: String) {
        presenterScope.launch {
            caregiverRepository.loadPatientDetails(patientId)
                .onStart { view?.showLoading() }
                .onCompletion { view?.hideLoading() }
                .catch { exception ->
                    view?.showErrorMessage("Failed to load patient details: ${exception.message}")
                }
                .collect { patient ->
                    if (patient != null) {
                        view?.showPatientDetails(patient)
                    } else {
                        view?.showErrorMessage("Patient details not found.")
                    }
                }
        }
    }

    /**
     * Add a new patient relationship via email lookup.
     */
    fun addPatient(caregiverId: String, patientEmail: String) {
        presenterScope.launch {
            view?.showLoading()
            try {
                caregiverRepository.addPatient(caregiverId, patientEmail)
                view?.onPatientAdded(patientEmail)
            } catch (e: Exception) {
                view?.showErrorMessage("Error adding patient: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }

    /**
     * Remove an existing patient relationship.
     */
    fun removePatient(patientId: String) {
        presenterScope.launch {
            view?.showLoading()
            try {
                caregiverRepository.removePatient(patientId)
                view?.onPatientRemoved(patientId)
            } catch (e: Exception) {
                view?.showErrorMessage("Error removing patient: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }

    /**
     * Send an invitation to a potential patient/user.
     */
    fun sendInvitation(email: String) {
        presenterScope.launch {
            view?.showLoading()
            try {
                caregiverRepository.sendInvitation(email)
                view?.onInvitationSent(email)
            } catch (e: Exception) {
                view?.showErrorMessage("Error sending invitation: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }

    /**
     * Load patient adherence data.
     */
    fun loadPatientAdherence(patientId: String) {
        presenterScope.launch {
            caregiverRepository.loadPatientAdherence(patientId)
                .onStart { view?.showLoading() }
                .onCompletion { view?.hideLoading() }
                .catch { exception ->
                    view?.showErrorMessage("Failed to load adherence: ${exception.message}")
                }
                .collect { adherenceLogs ->
                    view?.showAdherenceData(adherenceLogs)
                }
        }
    }

    /**
     * Load patient's list of medications.
     */
    fun loadPatientMedications(patientId: String) {
        presenterScope.launch {
            medicationRepository.loadPatientMedications(patientId) // Used medicationRepository here
                .onStart { view?.showLoading() }
                .onCompletion { view?.hideLoading() }
                .catch { exception ->
                    view?.showErrorMessage("Failed to load medications: ${exception.message}")
                }
                .collect { medications ->
                    view?.showMedications(medications)
                }
        }
    }
}