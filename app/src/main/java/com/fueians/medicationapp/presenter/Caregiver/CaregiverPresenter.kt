package com.fueians.medicationapp.presenter.Caregiver

import com.fueians.medicationapp.model.repository.CaregiverRepository
import com.fueians.medicationapp.model.repository.MedicationRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

class CaregiverPresenter(private var view: ICaregiverView?) {

    private val caregiverRepository = CaregiverRepository()
    private val medicationRepository = MedicationRepository()
    private val compositeDisposable = CompositeDisposable()

    fun attachView(view: ICaregiverView) {
        this.view = view
    }

    fun detachView() {
        view = null
        compositeDisposable.clear()
    }

    fun loadPatients(caregiverId: String) {
        view?.showLoading()
        val disposable = caregiverRepository.loadPatients(caregiverId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.displayPatients(it)
            }, {
                view?.hideLoading()
                view?.displayError(it.message ?: "Failed to load patients.")
            })
        compositeDisposable.add(disposable)
    }

    fun loadPatientDetails(patientId: String) {
        view?.showLoading()
        val disposable = caregiverRepository.loadPatientDetails(patientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                it?.let { view?.displayPatientDetails(it) }
            }, {
                view?.hideLoading()
                view?.displayError(it.message ?: "Failed to load patient details.")
            })
        compositeDisposable.add(disposable)
    }

    fun addPatient(caregiverId: String, patientEmail: String) {
        view?.showLoading()
        val disposable = caregiverRepository.addPatient(caregiverId, patientEmail)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.onPatientAdded()
            }, {
                view?.hideLoading()
                view?.displayError(it.message ?: "Failed to add patient.")
            })
        compositeDisposable.add(disposable)
    }

    fun removePatient(patientId: String, caregiverId: String) {
        view?.showLoading()
        val disposable = caregiverRepository.removePatient(patientId, caregiverId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.onPatientRemoved()
            }, {
                view?.hideLoading()
                view?.displayError(it.message ?: "Failed to remove patient.")
            })
        compositeDisposable.add(disposable)
    }

    fun sendInvitation(email: String) {
        val disposable = caregiverRepository.sendInvitation(email)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {
                view?.displayError(it.message ?: "Failed to send invitation.")
            })
        compositeDisposable.add(disposable)
    }

    fun loadPatientAdherence(patientId: String) {
        view?.showLoading()
        val disposable = caregiverRepository.loadPatientAdherence(patientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.displayPatientAdherence(it)
            }, {
                view?.hideLoading()
                view?.displayError(it.message ?: "Failed to load adherence data.")
            })
        compositeDisposable.add(disposable)
    }

    fun loadPatientMedications(patientId: String) {
        view?.showLoading()
        val disposable = medicationRepository.loadMedications(patientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.displayPatientMedications(it)
            }, {
                view?.hideLoading()
                view?.displayError(it.message ?: "Failed to load patient medications.")
            })
        compositeDisposable.add(disposable)
    }
}
