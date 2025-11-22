package com.fueians.medicationapp.presenter.User

import com.fueians.medicationapp.model.entities.UserEntity
import com.fueians.medicationapp.model.repository.UserRepository
import com.fueians.medicationapp.presenter.Iprofile.IProfileView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// =========================================================================
// 1. Placeholder Service
// =========================================================================

class SecurityManager {
    fun logout() {
        // In a real app, this would clear session tokens, etc.
        println("User logged out.")
    }
}

// =========================================================================
// 2. Presenter
// =========================================================================

class UserPresenter(private var view: IProfileView?) {

    // Dependencies are now private attributes, instantiated by the presenter.
    private val userRepository = UserRepository()
    private val securityManager = SecurityManager()

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
            try {
                val user = withContext(Dispatchers.IO) {
                    userRepository.getUserById(userId)
                }
                view?.hideLoading()
                user?.let { view?.displayUserProfile(it) }
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to load profile.")
            }
        }
    }

    fun updateUserProfile(user: UserEntity) {
        view?.showLoading()
        presenterScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    userRepository.updateUser(user)
                }
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
                withContext(Dispatchers.IO) {
                    userRepository.changePassword(userId, oldPassword, newPassword)
                }
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
            try {
                withContext(Dispatchers.IO) {
                    val user = userRepository.getUserById(userId)
                        ?: throw Exception("User not found.")
                    val updatedUser = user.copy(photoUrl = photoUri)
                    userRepository.updateUser(updatedUser)
                }
                view?.hideLoading()
                view?.onProfileUpdated()
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to update photo.")
            }
        }
    }

    fun logout() {
        presenterScope.launch {
            securityManager.logout()
            view?.onLogout()
        }
    }
}
