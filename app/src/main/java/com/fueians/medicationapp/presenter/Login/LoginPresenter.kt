package com.fueians.medicationapp.presenter.Login

import com.fueians.medicationapp.model.entities.UserEntity
import com.fueians.medicationapp.presenter.TestRepo.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

// =========================================================================
// 1. Result Sealed Class
// =========================================================================

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(val exception: Exception) : Result<Nothing>()
}

// =========================================================================
// 2. View Interface
// =========================================================================

interface LoginView {
    fun showLoginError(message: String)
    fun showLoading()
    fun hideLoading()
    // FIX: Changed signature to accept the UserEntity object for a more robust and type-safe approach.
    fun onLoginSuccess(user: UserEntity)
}


// =========================================================================
// 3. Presenter
// =========================================================================

class LoginPresenter(private var view: LoginView?) {

    // In a real app, this should be injected. For now, we mirror the original pattern.
    private val userRepository: UserRepository = UserRepository()

    // Use a lifecycle-aware scope (Main dispatcher with SupervisorJob) for proper cancellation.
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
            val result = userRepository.login(email, password)

            view?.hideLoading()

            when (result) {
                is Result.Success -> {
                    // FIX: Call the corrected success function with the user object.
                    view?.onLoginSuccess(result.data)
                }
                is Result.Failure -> {
                    view?.showLoginError(
                        result.exception.message ?: "Login failed"
                    )
                }
            }
        }
    }

    // Call this when the view (e.g., Fragment/Activity) is destroyed to prevent memory leaks.
    fun detachView() {
        view = null
        presenterScope.cancel()
    }
}
