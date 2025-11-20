package com.fueians.medicationapp.security

import at.favre.lib.crypto.bcrypt.BCrypt

/**
 * Interface for secure password hashing and verification.
 */
interface PasswordHasher {
    fun hash(password: String): String
    fun verify(password: String, hash: String): Boolean
}

class PasswordHasherImpl : PasswordHasher {
    // Recommended default cost for BCrypt
    private val COST = 12

    override fun hash(password: String): String {
        return BCrypt.withDefaults().hashToString(COST, password.toCharArray())
    }

    override fun verify(password: String, hash: String): Boolean {
        // BCrypt handles the salts internally within the hash string.
        val result = BCrypt.verifyer().verify(password.toCharArray(), hash.toCharArray())
        return result.verified
    }
}