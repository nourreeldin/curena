package com.fueians.medicationapp.presenter.Caregiver

import android.content.Context
import com.fueians.medicationapp.model.entities.CaregiverPatientEntity
import com.fueians.medicationapp.model.entities.MedicationEntity
import com.fueians.medicationapp.model.entities.UserEntity
import com.fueians.medicationapp.model.repository.CaregiverRepository
import com.fueians.medicationapp.model.repository.MedicationRepository
import com.fueians.medicationapp.view.interfaces.ICaregiverView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CaregiverPresenter(private var view: ICaregiverView?, context: Context) {

    private val caregiverRepository = CaregiverRepository(context)
    private val medicationRepository = MedicationRepository(context)

    private val presenterScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun attachView(view: ICaregiverView) {
        this.view = view
    }

    fun detachView() {
        view = null
        presenterScope.cancel()
    }

    // Load all patients for a caregiver
    fun loadPatients(caregiverId: String) {
        view?.showLoading()
        presenterScope.launch {
            try {
                caregiverRepository.getPatientsForCaregiver(caregiverId).collectLatest { patients ->
                    view?.hideLoading()
                    view?.displayPatients(patients) // error
                }
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to load patients.")
            }
        }
    }

    // Load patient details
    fun loadPatientDetails(patientId: String) {
        view?.showLoading()
        presenterScope.launch {
            try {
                val patient: UserEntity? = withContext(Dispatchers.IO) {
                    caregiverRepository.getPatientById(patientId)
                }
                view?.hideLoading()
                patient?.let { view?.displayPatientDetails(it) }
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to load patient details.")
            }
        }
    }

    // Add patient relationship
    fun addPatient(relationship: CaregiverPatientEntity) {
        view?.showLoading()
        presenterScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    caregiverRepository.addPatient(relationship)
                }
                view?.hideLoading()
                view?.onPatientAdded()
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to add patient.")
            }
        }
    }

    // Remove patient relationship
    fun removePatient(relationshipId: String) {
        view?.showLoading()
        presenterScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    caregiverRepository.removePatient(relationshipId)
                }
                view?.hideLoading()
                view?.onPatientRemoved()
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to remove patient.")
            }
        }
    }

    // Load medications of a patient
    fun loadPatientMedications(patientId: String) {
        view?.showLoading()
        presenterScope.launch {
            try {
                caregiverRepository.getPatientMedications(patientId).collectLatest { meds ->
                    view?.hideLoading()
                    view?.displayPatientMedications(meds)
                }
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to load patient medications.")
            }
        }
    }

    // Accept caregiver invitation
    fun acceptInvitation(relationshipId: String) {
        presenterScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    caregiverRepository.acceptInvitation(relationshipId)
                }
            } catch (e: Exception) {
                view?.displayError(e.message ?: "Failed to accept invitation.")
            }
        }
    }

    // Decline caregiver invitation
    fun declineInvitation(relationshipId: String) {
        presenterScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    caregiverRepository.declineInvitation(relationshipId)
                }
            } catch (e: Exception) {
                view?.displayError(e.message ?: "Failed to decline invitation.")
            }
        }
    }

    // Load active relationships
    fun loadActiveRelationships(caregiverId: String) {
        view?.showLoading()
        presenterScope.launch {
            try {
                caregiverRepository.getActiveRelationships(caregiverId).collectLatest { relationships ->
                    view?.hideLoading()
                    view?.displayActiveRelationships(relationships) // error
                }
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to load active relationships.")
            }
        }
    }

    // Load pending invitations
    fun loadPendingInvitations(patientId: String) {
        view?.showLoading()
        presenterScope.launch {
            try {
                caregiverRepository.getPendingInvitations(patientId).collectLatest { invitations ->
                    view?.hideLoading()
                    view?.displayPendingInvitations(invitations) // error
                }
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to load pending invitations.")
            }
        }
    }
}
