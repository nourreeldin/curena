package com.fueians.medicationapp.presenter.Signup

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

interface SignupView {
    fun showSignupError(message: String)
    fun showLoading()
    fun hideLoading()
    fun onSignupSuccess(user: UserEntity)
}

// =========================================================================
// 2. Presenter
// =========================================================================

class SignupPresenter(private var view: SignupView?) {

    // The repository is now a private attribute, instantiated by the presenter.
    private val userRepository = UserRepository()

    private val presenterScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun signup(name: String, email: String, password: String, confirmPassword: String) {
        // --- Synchronous UI Validations ---
        if (name.isBlank()) {
            view?.showSignupError("Full name is required")
            return
        }
        if (email.isBlank()) {
            view?.showSignupError("Email is required")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view?.showSignupError("Invalid email format")
            return
        }
        if (password.isBlank()) {
            view?.showSignupError("Password is required")
            return
        }
        if (!isStrongPassword(password)) {
            view?.showSignupError("Password is too weak. It must be 8+ characters and include uppercase, lowercase, a digit, and a special character.")
            return
        }
        if (password != confirmPassword) {
            view?.showSignupError("Passwords do not match")
            return
        }

        view?.showLoading()

        presenterScope.launch {
            try {
                // Run the blocking repository call on a background thread
                val newUser = withContext(Dispatchers.IO) {
                    userRepository.createAccount(name, email, password)
                }
                // Switch back to the Main thread to update the UI
                view?.hideLoading()
                view?.onSignupSuccess(newUser)
            } catch (e: Exception) {
                // Handle exceptions thrown by the repository
                view?.hideLoading()
                view?.showSignupError(e.message ?: "Signup failed due to an unknown error.")
            }
        }
    }

    /**
     * Checks if a password meets the required complexity criteria.
     */
    private fun isStrongPassword(password: String): Boolean {
        if (password.length < 8)
            return false
        if (!password.contains(Regex("[A-Z]")))
            return false
        if (!password.contains(Regex("[a-z]")))
            return false
        if (!password.contains(Regex("\\d")))
            return false
        if (!password.contains(Regex("[@$!%*?&]")))
            return false
        return true
    }

    fun detachView() {
        view = null
        presenterScope.cancel()
    }
}
