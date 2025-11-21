package com.fueians.medicationapp.presenter.EmailVerification

import com.fueians.medicationapp.model.repository.EmailVerificationRepository
import com.fueians.medicationapp.model.repository.VerificationResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

// =========================================================================
// 1. View Interface
// =========================================================================

/**
 * Interface for the Email Verification View (Activity or Fragment).
 */
interface IEmailVerificationView {
    fun showLoading()
    fun hideLoading()
    fun onEmailSentSuccess()
    fun onVerificationSuccess(userId: String)
    fun showVerificationError(message: String)
    fun showErrorMessage(message: String)
}

// =========================================================================
// 2. Presenter
// =========================================================================

/**
 * Handles the logic for sending verification emails and confirming codes.
 *
 * Responsibility: Manage the email verification flow (sending code, verifying code).
 * Usage: Used by email verification screens.
 */
class EmailVerificationPresenter(
    private var view: IEmailVerificationView?,
    private val verificationRepository: EmailVerificationRepository
) {
    private val presenterScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // --- Core Methods ---

    /**
     * Attach view when the Activity/Fragment is created.
     */
    fun attachView(view: IEmailVerificationView) {
        this.view = view
    }

    /**
     * Detach view when the Activity/Fragment is destroyed to prevent memory leaks.
     */
    fun detachView() {
        this.view = null
        presenterScope.cancel() // Cancel all coroutines launched in this scope
    }

    // --- Verification Flow ---

    /**
     * Sends a verification email/code request.
     */
    fun sendVerificationEmail(email: String) {
        presenterScope.launch {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                view?.showVerificationError("Invalid email format.")
                return@launch
            }

            view?.showLoading()
            try {
                verificationRepository.requestVerification(email)
                view?.onEmailSentSuccess()
            } catch (e: Exception) {
                view?.showErrorMessage("Failed to send verification email: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }

    /**
     * Alias for sendVerificationEmail for the resend button functionality.
     */
    fun resendCode(email: String) {
        sendVerificationEmail(email)
    }


    /**
     * Verifies the code provided by the user.
     */
    fun verifyCode(email: String, code: String) {
        presenterScope.launch {
            if (code.isBlank()) {
                view?.showVerificationError("Verification code is required.")
                return@launch
            }

            view?.showLoading()
            try {
                val result: VerificationResult = verificationRepository.confirmVerification(email, code)

                if (result.isSuccess && result.userId != null) {
                    view?.onVerificationSuccess(result.userId)
                } else {
                    view?.showVerificationError(result.message ?: "Verification failed.")
                }
            } catch (e: Exception) {
                view?.showErrorMessage("An error occurred during verification: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }
}