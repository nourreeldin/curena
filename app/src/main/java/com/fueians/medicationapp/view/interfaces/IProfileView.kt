package com.fueians.medicationapp.view.interfaces

import com.fueians.medicationapp.model.entities.UserEntity

interface IProfileView {

    // --- Loading States ---
    fun showLoading()
    fun hideLoading()

    // --- Profile Data ---
    fun displayUserProfile(user: UserEntity)
    fun onProfileUpdated()

    // --- Password Management ---
    fun onPasswordChanged()

    // --- Logout ---
    fun onLogout()

    // --- Error Handling ---
    fun displayError(message: String)
}
