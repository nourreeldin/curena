package com.fueians.medicationapp.model.security
import android.os.Build
import java.util.Base64
import java.util.Base64.getEncoder

class TokenManager(
    private val secretKey: String,
    private val expirationTime: Long = 3600000 // 1 hour in milliseconds
) {
    fun generateToken(userId: String, email: String): String {
        // Simple token format: userId:email:timestamp
        val timestamp = System.currentTimeMillis()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getEncoder().encodeToString(
                "$userId:$email:$timestamp".toByteArray()
            )
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }

    fun validateToken(token: String): Boolean {
        return try {
            val decoded = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String(Base64.getDecoder().decode(token))
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val parts = decoded.split(":")
            if (parts.size != 3) return false

            val timestamp = parts[2].toLong()
            !isTokenExpired(token)
        } catch (e: Exception) {
            false
        }
    }

    fun refreshToken(token: String): String {
        if (!validateToken(token)) {
            throw IllegalArgumentException("Invalid token")
        }

        val userId = extractUserId(token)
        val decoded = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String(Base64.getDecoder().decode(token))
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val email = decoded.split(":")[1]

        return generateToken(userId, email)
    }

    fun extractUserId(token: String): String {
        val decoded = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String(Base64.getDecoder().decode(token))
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        return decoded.split(":")[0]
    }

    fun isTokenExpired(token: String): Boolean {
        return try {
            val decoded = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String(Base64.getDecoder().decode(token))
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val timestamp = decoded.split(":")[2].toLong()
            val currentTime = System.currentTimeMillis()
            (currentTime - timestamp) > expirationTime
        } catch (e: Exception) {
            true
        }
    }
}