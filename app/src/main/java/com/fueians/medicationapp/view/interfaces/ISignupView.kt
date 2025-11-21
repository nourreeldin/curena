package com.fueians.medicationapp.view.interfaces

import com.fueians.medicationapp.model.entities.UserEntity

interface ISignupView {
    fun showLoading()
    fun hideLoading()
    fun showRegistrationSuccess()
    fun showError(message: String)
}
