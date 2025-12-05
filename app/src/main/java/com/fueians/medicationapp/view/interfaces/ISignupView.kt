package com.fueians.medicationapp.view.interfaces
interface ISignupView {
    fun showLoading()
    fun hideLoading()
    fun showRegistrationSuccess()
    fun showError(message: String)
}
