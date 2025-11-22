package com.fueians.medicationapp.presenter.Caregiver

import com.fueians.medicationapp.model.repository.CaregiverRepository
import com.fueians.medicationapp.model.repository.MedicationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CaregiverPresenter(private var view: ICaregiverView?) {

    // Dependencies are now private attributes, instantiated by the presenter.
    private val caregiverRepository = CaregiverRepository()
    private val medicationRepository = MedicationRepository()

    private val presenterScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun attachView(view: ICaregiverView) {
        this.view = view
    }

    fun detachView() {
        view = null
        presenterScope.cancel()
    }

    fun loadPatients(caregiverId: String) {
        view?.showLoading()
        presenterScope.launch {
            try {
                val patients = withContext(Dispatchers.IO) {
                    caregiverRepository.loadPatients(caregiverId)
                }
                view?.hideLoading()
                view?.displayPatients(patients)
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to load patients.")
            }
        }
    }

    fun loadPatientDetails(patientId: String) {
        view?.showLoading()
        presenterScope.launch {
            try {
                val patient = withContext(Dispatchers.IO) {
                    caregiverRepository.loadPatientDetails(patientId)
                }
                view?.hideLoading()
                patient?.let { view?.displayPatientDetails(it) }
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to load patient details.")
            }
        }
    }

    fun addPatient(caregiverId: String, patientEmail: String) {
        view?.showLoading()
        presenterScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    caregiverRepository.addPatient(caregiverId, patientEmail)
                }
                view?.hideLoading()
                view?.onPatientAdded()
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to add patient.")
            }
        }
    }

    fun removePatient(patientId: String, caregiverId: String) {
        view?.showLoading()
        presenterScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    caregiverRepository.removePatient(patientId, caregiverId)
                }
                view?.hideLoading()
                view?.onPatientRemoved()
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to remove patient.")
            }
        }
    }

    fun sendInvitation(email: String) {
        presenterScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    caregiverRepository.sendInvitation(email)
                }
            } catch (e: Exception) {
                view?.displayError(e.message ?: "Failed to send invitation.")
            }
        }
    }

    fun loadPatientAdherence(patientId: String) {
        view?.showLoading()
        presenterScope.launch {
            try {
                val adherence = withContext(Dispatchers.IO) {
                    caregiverRepository.loadPatientAdherence(patientId)
                }
                view?.hideLoading()
                view?.displayPatientAdherence(adherence)
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to load adherence data.")
            }
        }
    }

    fun loadPatientMedications(patientId: String) {
        view?.showLoading()
        presenterScope.launch {
            try {
                val medications = withContext(Dispatchers.IO) {
                    medicationRepository.loadMedications(patientId)
                }
                view?.hideLoading()
                view?.displayPatientMedications(medications)
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to load patient medications.")
            }
        }
    }
}
