package com.fueians.medicationapp.presenter.ForgetPassword

import com.fueians.medicationapp.model.repository.ForgetPasswordRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

// =========================================================================
// 1. View Interface
// =========================================================================

/**
 * Interface for the Forgot Password View (Activity or Fragment).
 */
interface IForgotPasswordView {
    fun showLoading()
    fun hideLoading()
    fun onResetRequestSuccess()
    fun onPasswordResetSuccess()
    fun showValidationError(message: String)
    fun showErrorMessage(message: String)
}

// =========================================================================
// 2. Presenter
// =========================================================================

/**
 * Handles the logic for the forgot password flow, managing email requests and password confirmation.
 *
 * Responsibility: Manage the two-step password reset flow.
 * Usage: Used by forgot password screens.
 */
class ForgetPasswordPresenter(
    private var view: IForgotPasswordView?,
    private val passwordRepository: ForgetPasswordRepository
) {
    private val presenterScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // --- Core Methods ---

    /**
     * Attach view when the Activity/Fragment is created.
     */
    fun attachView(view: IForgotPasswordView) {
        this.view = view
    }

    /**
     * Detach view when the Activity/Fragment is destroyed to prevent memory leaks.
     */
    fun detachView() {
        this.view = null
        presenterScope.cancel() // Cancel all coroutines launched in this scope
    }

    // --- Reset Step 1: Request Code ---

    /**
     * Sends a request to the backend to initiate the password reset process via email.
     */
    fun requestPasswordReset(email: String) {
        presenterScope.launch {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                view?.showValidationError("Please enter a valid email address.")
                return@launch
            }

            view?.showLoading()
            try {
                val success = passwordRepository.requestPasswordReset(email)
                if (success) {
                    view?.onResetRequestSuccess()
                } else {
                    // Although success is true, sometimes the service returns false internally for minor reasons
                    view?.showErrorMessage("Failed to send reset request. Please try again.")
                }
            } catch (e: Exception) {
                view?.showErrorMessage("Error requesting password reset: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }

    // --- Reset Step 2: Confirm Code and Set New Password ---

    /**
     * Confirms the reset code and sets the new password.
     */
    fun confirmPasswordReset(email: String, code: String, newPassword: String, confirmPassword: String) {
        presenterScope.launch {
            // --- Input Validation ---
            if (code.isBlank()) {
                view?.showValidationError("Verification code is required.")
                return@launch
            }
            if (newPassword.isBlank()) {
                view?.showValidationError("New password is required.")
                return@launch
            }
            if (newPassword != confirmPassword) {
                view?.showValidationError("Passwords do not match.")
                return@launch
            }
            if (!isStrongPassword(newPassword)) {
                view?.showValidationError("Password must be 8+ characters and include uppercase, lowercase, a digit, and a special character.")
                return@launch
            }
            // --- End Validation ---

            view?.showLoading()
            try {
                val result = passwordRepository.confirmPasswordReset(email, code, newPassword)

                if (result.isSuccess) {
                    view?.onPasswordResetSuccess()
                } else {
                    view?.showValidationError(result.message ?: "Password reset failed due to unknown error.")
                }
            } catch (e: Exception) {
                view?.showErrorMessage("An error occurred during password confirmation: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }

    /**
     * Checks if a password meets the required complexity criteria (reused from SignupPresenter).
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
}