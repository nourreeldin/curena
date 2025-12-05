package com.fueians.medicationapp.view.interfaces

interface IEmailVerificationView {
    fun showLoading()
    fun hideLoading()
    fun onEmailSentSuccess()
    fun onVerificationSuccess(userId: String)
    fun showVerificationError(message: String)
    fun showErrorMessage(message: String)
}