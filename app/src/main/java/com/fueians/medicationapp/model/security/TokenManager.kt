package com.fueians.medicationapp.model.security

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.fueians.medicationapp.model.entities.UserEntity
import java.util.Base64

class TokenManager(
    context: Context,
    private val secretKey: String = "default_secret_key",
    private val expirationTime: Long = 3600000 // 1 hour in milliseconds
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private companion object {
        const val KEY_TOKEN = "user_token"
        const val KEY_USER_ID = "user_id"
        const val KEY_USER_EMAIL = "user_email"
        const val KEY_USER_NAME = "user_name"
    }

    fun generateToken(userId: String, email: String): String {
        // Simple token format: userId:email:timestamp
        val timestamp = System.currentTimeMillis()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Base64.getEncoder().encodeToString(
                "$userId:$email:$timestamp".toByteArray()
            )
        } else {
            android.util.Base64.encodeToString(
                "$userId:$email:$timestamp".toByteArray(),
                android.util.Base64.DEFAULT
            )
        }
    }

    fun validateToken(token: String): Boolean {
        return try {
            val decoded = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String(Base64.getDecoder().decode(token))
            } else {
                String(android.util.Base64.decode(token, android.util.Base64.DEFAULT))
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
            String(android.util.Base64.decode(token, android.util.Base64.DEFAULT))
        }
        val email = decoded.split(":")[1]

        return generateToken(userId, email)
    }

    fun extractUserId(token: String): String {
        val decoded = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String(Base64.getDecoder().decode(token))
        } else {
            String(android.util.Base64.decode(token, android.util.Base64.DEFAULT))
        }
        return decoded.split(":")[0]
    }

    fun isTokenExpired(token: String): Boolean {
        return try {
            val decoded = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String(Base64.getDecoder().decode(token))
            } else {
                String(android.util.Base64.decode(token, android.util.Base64.DEFAULT))
            }
            val timestamp = decoded.split(":")[2].toLong()
            val currentTime = System.currentTimeMillis()
            (currentTime - timestamp) > expirationTime
        } catch (e: Exception) {
            true
        }
    }

    // New functions needed by AuthRepository and AuthService
    fun saveToken(token: String) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply()
    }

    fun clearToken() {
        sharedPreferences.edit().clear().apply()
    }



    fun hasValidToken(): Boolean {
        val token = sharedPreferences.getString(KEY_TOKEN, null)
        return token != null && validateToken(token)
    }

    fun saveUserInfo(user: UserEntity) {
        sharedPreferences.edit().apply {
            putString(KEY_USER_ID, user.id)
            putString(KEY_USER_EMAIL, user.email)

            apply()
        }
    }

    fun getCurrentUser(): UserEntity? {
        val userId = sharedPreferences.getString(KEY_USER_ID, null)
        val email = sharedPreferences.getString(KEY_USER_EMAIL, null)


        return if (userId != null && email != null) {
            UserEntity(id = userId, email = email)
        } else {
            null
        }
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }
}