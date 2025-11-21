package com.fueians.medicationapp.presenter.Signup

import com.fueians.medicationapp.model.entities.UserEntity
import com.fueians.medicationapp.presenter.TestRepo.UserRepository
import com.fueians.medicationapp.presenter.Login.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

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

    // In a real app, this should be injected.
    private val userRepository: UserRepository = UserRepository()

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
            val result = userRepository.createAccount(name, email, password)

            view?.hideLoading()

            when (result) {
                is Result.Success -> {
                    view?.onSignupSuccess(result.data)
                }
                is Result.Failure -> {
                    view?.showSignupError(
                        result.exception.message ?: "Signup failed due to an unknown error."
                    )
                }
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

        if (!password.contains(Regex("[@\$!%*?&]")))
            return false

        return true
    }

    // Call this when the view (e.g., Fragment/Activity) is destroyed to prevent memory leaks.
    fun detachView() {
        view = null
        presenterScope.cancel()
    }
}
