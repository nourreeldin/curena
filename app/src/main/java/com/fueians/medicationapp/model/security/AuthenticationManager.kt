package com.fueians.medicationapp.model.security

import android.content.Context
import android.util.Log

class AuthenticationManager(
    private val context: Context
) {

    private val securityManager = SecurityManager.getInstance(context)

    private var initialized = false

    private val PREF_TOKEN = "auth_token"
    private val PREF_LAST_ACTIVE = "last_active"
    private val PREF_FAILED_ATTEMPTS = "failed_attempts"

    private val sessionTimeoutMinutes: Int
        get() = securityManager.getSecurityEventLog()
            .lastOrNull()?.timestamp?.let { securityManager.detectSecurityThreats(); 30 } ?: 30

    fun initialize() {
        if (initialized) return
        initialized = true
        Log.d("AuthenticationManager", "Initialized")
    }

    /**
     * Perform login using SecurityManager hashing + secure storage
     */
    fun login(email: String, password: String): Boolean {
        val policy = SecurityPolicy.default()

        // Check login attempts
        val attempts = securityManager.retrieveSecureData(PREF_FAILED_ATTEMPTS)?.toIntOrNull() ?: 0
        if (attempts >= policy.maxLoginAttempts) {
            Log.e("AuthenticationManager", "Account locked due to too many attempts.")
            return false
        }

        // Fake DB logic (replace with real DAO)
        val storedHash = securityManager.retrieveSecureData("user_$email")

        if (storedHash == null || !securityManager.verifyPassword(password, storedHash)) {
            securityManager.storeSecureData(PREF_FAILED_ATTEMPTS, (attempts + 1).toString())
            return false
        }

        // Reset failed attempts
        securityManager.storeSecureData(PREF_FAILED_ATTEMPTS, "0")

        // Issue token
        val token = securityManager.generateToken()
        securityManager.storeSecureData(PREF_TOKEN, token)
        securityManager.storeSecureData(PREF_LAST_ACTIVE, System.currentTimeMillis().toString())

        securityManager.logSecurityEvent(
            SecurityEvent(
                SecurityEvent.EventType.LOGIN_SUCCESS,
                "User logged in: $email"
            )
        )

        return true
    }

    /**
     * Logout (clear everything)
     */
    fun logout() {
        securityManager.storeSecureData(PREF_TOKEN, "")
        securityManager.storeSecureData(PREF_LAST_ACTIVE, "0")

        securityManager.logSecurityEvent(
            SecurityEvent(
                SecurityEvent.EventType.LOGOUT,
                "User logged out"
            )
        )
    }

    /**
     * Check if the user still has a valid session token AND is within timeout
     */
    fun isUserLoggedIn(): Boolean {
        val token = securityManager.retrieveSecureData(PREF_TOKEN)
        if (token.isNullOrEmpty()) return false

        if (!securityManager.validateToken(token)) return false

        val lastActive = securityManager.retrieveSecureData(PREF_LAST_ACTIVE)?.toLongOrNull()
            ?: return false

        val policy = securityManager.getSecurityPolicy()
        val timeoutMs = policy.sessionTimeoutMinutes * 60_000L

        val now = System.currentTimeMillis()
        if (now - lastActive > timeoutMs) {
            logout()
            return false
        }

        // refresh last active
        securityManager.storeSecureData(PREF_LAST_ACTIVE, now.toString())
        return true
    }
}
