package com.fueians.medicationapp.presenter.User

import com.fueians.medicationapp.model.entities.UserEntity
import com.fueians.medicationapp.presenter.TestRepo.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

// =========================================================================
// 1. Placeholder Service & View Interface
// =========================================================================

class SecurityManager {
    fun logout() {
        // In a real app, this would clear session tokens, etc.
        println("User logged out.")
    }
}

interface IProfileView {
    fun showLoading()
    fun hideLoading()
    fun displayUserProfile(user: UserEntity)
    fun displayError(message: String)
    fun onProfileUpdated()
    fun onPasswordChanged()
    fun onLogout()
}

// =========================================================================
// 2. Presenter
// =========================================================================

class UserPresenter(
    private var view: IProfileView?,
    // In a real app, these would be injected.
    private val userRepository: UserRepository,
    private val securityManager: SecurityManager = SecurityManager()
) {

    private val presenterScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun attachView(view: IProfileView) {
        this.view = view
    }

    fun detachView() {
        view = null
        presenterScope.cancel()
    }

    fun loadUserProfile(userId: String) {
        view?.showLoading()
        presenterScope.launch {
            // Assuming the repo has a method to get user by ID
            userRepository.getUserById(userId).collect {
                it?.let { user -> view?.displayUserProfile(user) }
                view?.hideLoading()
            }
        }
    }

    fun updateUserProfile(user: UserEntity) {
        view?.showLoading()
        presenterScope.launch {
            try {
                userRepository.updateUser(user) // Assumes repo has this method
                view?.hideLoading()
                view?.onProfileUpdated()
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to update profile.")
            }
        }
    }

    fun changePassword(userId: String, oldPassword: String, newPassword: String) {
        view?.showLoading()
        presenterScope.launch {
            try {
                // This logic would be more complex, involving rehashing
                // For now, we assume a simple repository method
                userRepository.changePassword(userId, oldPassword, newPassword)
                view?.hideLoading()
                view?.onPasswordChanged()
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to change password.")
            }
        }
    }

    fun updateProfilePhoto(userId: String, photoUri: String) {
        view?.showLoading()
        presenterScope.launch {
            // In a real app, you would upload the photo and update the user's photo URL
            println("Updating photo for user $userId with URI: $photoUri")
            // Simulate updating the user profile with a new photo URL
            val user = userRepository.getUserById(userId).firstOrNull()?.copy(photoUrl = photoUri)
            if(user != null) {
                 userRepository.updateUser(user)
            }
            view?.hideLoading()
            view?.onProfileUpdated()
        }
    }

    fun logout() {
        presenterScope.launch {
            securityManager.logout()
            view?.onLogout()
        }
    }
}