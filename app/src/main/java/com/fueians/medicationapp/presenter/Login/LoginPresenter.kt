package com.fueians.medicationapp.presenter.Login

import com.fueians.medicationapp.model.entities.UserEntity
import com.fueians.medicationapp.model.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// =========================================================================
// 1. View Interface
// =========================================================================

interface LoginView {
    fun showLoginError(message: String)
    fun showLoading()
    fun hideLoading()
    fun onLoginSuccess(user: UserEntity)
}

// =========================================================================
// 2. Presenter
// =========================================================================

class LoginPresenter(private var view: LoginView?) {

    // The repository is now a private attribute, instantiated by the presenter.
    private val userRepository = UserRepository()

    private val presenterScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun login(email: String, password: String) {
        // --- Synchronous UI Validations ---
        if (email.isBlank()) {
            view?.showLoginError("Email is required")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view?.showLoginError("Invalid email format")
            return
        }
        if (password.isBlank()) {
            view?.showLoginError("Password is required")
            return
        }

        view?.showLoading()

        presenterScope.launch {
            try {
                // Run the blocking repository call on a background thread
                val user = withContext(Dispatchers.IO) {
                    userRepository.login(email, password)
                }
                // Switch back to the Main thread to update the UI
                view?.hideLoading()
                view?.onLoginSuccess(user)
            } catch (e: Exception) {
                // Handle exceptions thrown by the repository
                view?.hideLoading()
                view?.showLoginError(e.message ?: "Login failed")
            }
        }
    }

    fun detachView() {
        view = null
        presenterScope.cancel()
    }
}
