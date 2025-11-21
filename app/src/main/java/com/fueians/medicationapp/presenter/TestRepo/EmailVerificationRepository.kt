package com.fueians.medicationapp.model.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

// =========================================================================
// 1. Placeholder Entities and Models
// =========================================================================

/**
 * Result of an email verification attempt.
 */
data class VerificationResult(
    val isSuccess: Boolean,
    val message: String? = null,
    val userId: String? = null // If successful, returns the user ID
)

// =========================================================================
// 2. External Service (API Client and Verification Logic)
// =========================================================================

/**
 * Placeholder for the low-level authentication API client.
 */
class AuthAPIClient {
    /** Simulates sending a verification email/code request to a remote server. */
    suspend fun sendVerificationRequest(email: String): Boolean = withContext(Dispatchers.IO) {
        delay(500) // Simulate network delay
        if (email.contains("fail")) throw Exception("API error during send request.")
        println("Verification email sent to $email.")
        return@withContext true
    }

    /** Simulates confirming the verification code with the remote server. */
    suspend fun confirmVerificationCode(email: String, code: String): VerificationResult = withContext(Dispatchers.IO) {
        delay(700) // Simulate network delay and code processing
        return@withContext if (code == "123456") {
            VerificationResult(
                isSuccess = true,
                userId = "user-${email.hashCode()}"
            )
        } else {
            VerificationResult(
                isSuccess = false,
                message = "The provided code is invalid. Please try again or resend the email."
            )
        }
    }
}

/**
 * Service that wraps the API client for email verification logic.
 */
class EmailVerificationService(private val apiClient: AuthAPIClient) {
    suspend fun requestVerification(email: String): Boolean {
        return apiClient.sendVerificationRequest(email)
    }

    suspend fun verifyCode(email: String, code: String): VerificationResult {
        return apiClient.confirmVerificationCode(email, code)
    }
}


// =========================================================================
// 3. Repository
// =========================================================================

/**
 * Handles the abstraction layer for email verification operations.
 *
 * Responsibility: Abstract the communication layer for verification (e.g., calling an Auth API).
 */
class EmailVerificationRepository(
    private val verificationService: EmailVerificationService,
    private val backgroundDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    /**
     * Requests the backend to send a verification code or link to the specified email.
     */
    suspend fun requestVerification(email: String): Boolean = withContext(backgroundDispatcher) {
        // Simple synchronous call to the service
        return@withContext verificationService.requestVerification(email)
    }

    /**
     * Confirms the provided verification code against the backend.
     */
    suspend fun confirmVerification(email: String, code: String): VerificationResult = withContext(backgroundDispatcher) {
        // Simple synchronous call to the service
        return@withContext verificationService.verifyCode(email, code)
    }
}