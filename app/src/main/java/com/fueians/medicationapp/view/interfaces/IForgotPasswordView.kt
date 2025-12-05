package com.fueians.medicationapp.view.interfaces

interface IForgotPasswordView {
    fun showLoading()
    fun hideLoading()
    fun onResetRequestSuccess()
    fun onPasswordResetSuccess()
    fun showValidationError(message: String)
    fun showErrorMessage(message: String)
}