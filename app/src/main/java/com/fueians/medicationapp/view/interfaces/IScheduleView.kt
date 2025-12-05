package com.fueians.medicationapp.view.interfaces

import com.fueians.medicationapp.model.entities.MedicationScheduleEntity

interface IScheduleView {
    // --- Loading States ---
    fun showLoading()
    fun hideLoading()

    fun displaySchedules(schedules: List<MedicationScheduleEntity>)
    fun onScheduleCreated()
    fun onScheduleUpdated()
    fun onScheduleDeleted()
    fun displayError(message: String)

    fun displayMissedDoses(doses: List<MedicationScheduleEntity>)
}
