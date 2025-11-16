package com.fueians.medicationapp.model.security

import at.favre.lib.crypto.bcrypt.BCrypt

class PasswordHasher(
    private val algorithm: String = "BCrypt",
    private val saltLength: Int = 10
) {

    fun hash(password: String): String {
        return BCrypt.withDefaults().hashToString(saltLength, password.toCharArray())
    }

    fun verify(password: String, hash: String): Boolean {
        return try {
            val result = BCrypt.verifyer().verify(password.toCharArray(), hash)
            result.verified
        } catch (e: Exception) {
            false
        }
    }

    fun generateSalt(): String {
        // This library handles salt generation internally
        return ""
    }
}