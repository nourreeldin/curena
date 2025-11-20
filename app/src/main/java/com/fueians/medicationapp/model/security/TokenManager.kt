package com.fueians.medicationapp.security

import android.content.Context
import java.util.concurrent.atomic.AtomicReference

/**
 * Manages the storage and retrieval of authentication tokens.
 */
interface TokenManager {
    fun getAuthToken(): String?
    fun saveAuthToken(token: String)
    fun clearAuthToken()
    fun isTokenValid(): Boolean
}

class TokenManagerImpl(private val context: Context) : TokenManager {
    // In a production app, replace this with SharedPreferences backed by EncryptedSharedPreferences (Jetpack Security)
    private val currentToken = AtomicReference<String?>()

    override fun getAuthToken(): String? {
        // Replace with retrieval from secure storage
        return currentToken.get()
    }

    override fun saveAuthToken(token: String) {
        // Replace with storage into secure storage
        currentToken.set(token)
    }

    override fun clearAuthToken() {
        // Replace with clearing from secure storage
        currentToken.set(null)
    }

    // Simple check; actual validity should be checked against an expiration date or backend call
    override fun isTokenValid(): Boolean {
        return !currentToken.get().isNullOrEmpty()
    }
}