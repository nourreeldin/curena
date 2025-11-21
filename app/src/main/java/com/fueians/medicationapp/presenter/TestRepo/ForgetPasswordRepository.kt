package com.fueians.medicationapp.model.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

// =========================================================================
// 1. Placeholder Entities and Models
// =========================================================================

/**
 * Result of a password reset confirmation attempt.
 */
data class PasswordResetResult(
    val isSuccess: Boolean,
    val message: String? = null
)

// =========================================================================
// 2. External Service (API Client and Logic)
// =========================================================================

/**
 * Placeholder for the low-level authentication API client, used for external communication.
 * Note: Reusing the package structure, assuming AuthAPIClient is defined in this module's scope.
 */
class AuthAPIClient {
    /** Simulates sending a password reset link/code request to a remote server. */
    suspend fun sendPasswordResetRequest(email: String): Boolean = withContext(Dispatchers.IO) {
        delay(600) // Simulate network delay
        if (email.contains("servererror")) throw Exception("API error during reset request.")
        println("Password reset request sent to $email.")
        return@withContext true
    }

    /** Simulates confirming the reset code and setting a new password. */
    suspend fun confirmNewPassword(email: String, code: String, newPassword: String): PasswordResetResult = withContext(Dispatchers.IO) {
        delay(900) // Simulate network delay and processing
        return@withContext if (code == "RESET123" && newPassword.isNotEmpty()) {
            PasswordResetResult(
                isSuccess = true,
                message = "Password successfully updated for user $email."
            )
        } else if (code != "RESET123") {
            PasswordResetResult(
                isSuccess = false,
                message = "Invalid or expired reset code."
            )
        } else {
            PasswordResetResult(
                isSuccess = false,
                message = "Password update failed."
            )
        }
    }
}

/**
 * Service that wraps the API client for forgot password logic.
 */
class ForgotPasswordService(private val apiClient: AuthAPIClient) {
    suspend fun requestReset(email: String): Boolean {
        return apiClient.sendPasswordResetRequest(email)
    }

    suspend fun confirmReset(email: String, code: String, newPassword: String): PasswordResetResult {
        return apiClient.confirmNewPassword(email, code, newPassword)
    }
}


// =========================================================================
// 3. Repository
// =========================================================================

/**
 * Handles the abstraction layer for the forgot password flow.
 *
 * Responsibility: Abstract the communication layer for password reset (e.g., calling an Auth API).
 */
class ForgetPasswordRepository(
    private val forgotPasswordService: ForgotPasswordService,
    private val backgroundDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    /**
     * Requests the backend to send a password reset link or code to the specified email.
     */
    suspend fun requestPasswordReset(email: String): Boolean = withContext(backgroundDispatcher) {
        return@withContext forgotPasswordService.requestReset(email)
    }

    /**
     * Confirms the reset code and sets a new password for the user.
     */
    suspend fun confirmPasswordReset(email: String, code: String, newPassword: String): PasswordResetResult = withContext(backgroundDispatcher) {
        return@withContext forgotPasswordService.confirmReset(email, code, newPassword)
    }
}