package com.fueians.medicationapp.presenter.ForgetPassword

import com.fueians.medicationapp.view.interfaces.IForgotPasswordView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ForgetPasswordPresenter(
    private var view: IForgotPasswordView?
) {
    private val presenterScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun attachView(view: IForgotPasswordView) {
        this.view = view
    }

    fun detachView() {
        this.view = null
        presenterScope.cancel()
    }

    /**
     * Step 1: Request password reset (mock)
     */
    fun requestPasswordReset(email: String) {
        presenterScope.launch {
            if (email.isBlank()) {
                view?.showValidationError("Email is required")
                return@launch
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                view?.showValidationError("Invalid email format")
                return@launch
            }

            view?.showLoading()
            // Simulate network delay
            delay(1000)
            view?.onResetRequestSuccess()
            view?.hideLoading()
        }
    }

    /**
     * Step 2: Confirm new password (mock)
     */
    fun confirmPasswordReset(code: String, newPassword: String, confirmPassword: String) {
        presenterScope.launch {
            if (code.isBlank()) {
                view?.showValidationError("Verification code is required")
                return@launch
            }
            if (newPassword.isBlank()) {
                view?.showValidationError("New password is required")
                return@launch
            }
            if (newPassword != confirmPassword) {
                view?.showValidationError("Passwords do not match")
                return@launch
            }
            if (!isStrongPassword(newPassword)) {
                view?.showValidationError("Password must be 8+ chars with uppercase, lowercase, digit, and special char")
                return@launch
            }

            view?.showLoading()
            // Simulate network delay
            delay(1000)
            view?.onPasswordResetSuccess()
            view?.hideLoading()
        }
    }

    private fun isStrongPassword(password: String): Boolean {
        if (password.length < 8) return false
        if (!password.any { it.isUpperCase() }) return false
        if (!password.any { it.isLowerCase() }) return false
        if (!password.any { it.isDigit() }) return false
        if (!password.any { "!@#$%^&*()_+[]{}|;:,.<>?/".contains(it) }) return false
        return true
    }
}
