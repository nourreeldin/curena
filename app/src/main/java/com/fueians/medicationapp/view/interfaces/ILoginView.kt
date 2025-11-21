package com.fueians.medicationapp.view.interfaces

import com.fueians.medicationapp.model.entities.UserEntity

interface ILoginView {
    fun showLoading()
    fun hideLoading()
    fun showLoginSuccess(user: UserEntity)
    fun showLoginError(message: String)
    fun navigateToHome()
}
