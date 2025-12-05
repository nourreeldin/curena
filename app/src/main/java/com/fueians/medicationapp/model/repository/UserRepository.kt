package com.fueians.medicationapp.model.repository

import android.content.Context
import com.fueians.medicationapp.model.dao.UserDao
import com.fueians.medicationapp.model.entities.UserEntity
import com.fueians.medicationapp.model.database.AppDatabase
import com.fueians.medicationapp.model.security.SecurityManager
import com.fueians.medicationapp.model.security.AuthenticationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Failure(val exception: Exception) : Result<Nothing>()
}

class UserRepository(context: Context) {

    private val userDao: UserDao by lazy {
        AppDatabase.getInstance(context).userDao()
    }
    private val securityManager: SecurityManager by lazy {
        SecurityManager.getInstance(context)
    }
    private val authenticationManager: AuthenticationManager by lazy {
        AuthenticationManager(context)
    }

    /**
     * Register new user
     */
    suspend fun register(email: String, password: String, name: String): Result<UserEntity> =
        withContext(Dispatchers.IO) {
            try {
                // Validate password
                if (!isPasswordValid(password)) {
                    return@withContext Result.Failure(
                        Exception("Password does not meet security requirements")
                    )
                }

                // Hash password
                val passwordHash = securityManager.hashPassword(password)

                // Create user entity
                val user = UserEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    name = name,
                    email = email,
                    isEmailVerified = false
                )

                // Save user to database
                userDao.insertUser(user)

                // Store password hash securely
                securityManager.storeSecureData("password_${user.id}", passwordHash)

                Result.Success(user)
            } catch (e: Exception) {
                Result.Failure(e)
            }
        }

    /**
     * Login user
     */
    suspend fun login(email: String, password: String): Result<UserEntity> =
        withContext(Dispatchers.IO) {
            try {
                // Get user by email
                val user = userDao.getUserByEmail(email)
                    ?: return@withContext Result.Failure(Exception("User not found"))

                // Verify password using AuthenticationManager
                val isValid = authenticationManager.login(email, password)
                if (!isValid) {
                    return@withContext Result.Failure(Exception("Invalid credentials"))
                }

                Result.Success(user)
            } catch (e: Exception) {
                Result.Failure(e)
            }
        }

    /**
     * Logout user
     */
    fun logout() {
        authenticationManager.logout()
    }

    /**
     * Get current user
     */
    suspend fun getCurrentUser(): UserEntity? = withContext(Dispatchers.IO) {
        val userId = securityManager.retrieveSecureData("current_user_id")
        userId?.let { userDao.getUserById(it) }
    }

    /**
     * Check if user is logged in
     */
    fun isUserLoggedIn(): Boolean {
        return authenticationManager.isUserLoggedIn()
    }

    /**
     * Update user profile
     */
    suspend fun updateUser(user: UserEntity): Result<UserEntity> = withContext(Dispatchers.IO) {
        try {
            userDao.updateUser(user)
            Result.Success(user)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    /**
     * Get user by ID
     */
    suspend fun getUserById(userId: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getUserById(userId)
    }

    /**
     * Get user by email
     */
    suspend fun getUserByEmail(email: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getUserByEmail(email)
    }

    /**
     * Change password
     */
    suspend fun changePassword(
        userId: String,
        oldPassword: String,
        newPassword: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Verify old password
            val storedHash = securityManager.retrieveSecureData("password_$userId")
                ?: return@withContext Result.Failure(Exception("Password not found"))

            if (!securityManager.verifyPassword(oldPassword, storedHash)) {
                return@withContext Result.Failure(Exception("Invalid old password"))
            }

            // Validate new password
            if (!isPasswordValid(newPassword)) {
                return@withContext Result.Failure(
                    Exception("New password does not meet security requirements")
                )
            }

            // Hash and store new password
            val newHash = securityManager.hashPassword(newPassword)
            securityManager.storeSecureData("password_$userId", newHash)

            // Log security event
            securityManager.logSecurityEvent(
                com.fueians.medicationapp.model.security.SecurityEvent(
                    com.fueians.medicationapp.model.security.SecurityEvent.EventType.PASSWORD_CHANGED,
                    "Password changed for user: $userId"
                )
            )

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    /**
     * Delete user account
     */
    suspend fun deleteUser(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val user = userDao.getUserById(userId)
                ?: return@withContext Result.Failure(Exception("User not found"))

            userDao.deleteUser(user)
            securityManager.clearSecureData()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    /**
     * Validate password against security policy
     */
    private fun isPasswordValid(password: String): Boolean {
        val policy = securityManager.getSecurityPolicy()

        if (password.length < policy.minPasswordLength) return false
        if (policy.requireUppercase && !password.any { it.isUpperCase() }) return false
        if (policy.requireNumbers && !password.any { it.isDigit() }) return false
        if (policy.requireSpecialChars && !password.any { !it.isLetterOrDigit() }) return false

        return true
    }

    /**
     * Verify email
     */
    suspend fun verifyEmail(userId: String, token: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val user = userDao.getUserById(userId)
                    ?: return@withContext Result.Failure(Exception("User not found"))

                // Validate token
                val storedToken = securityManager.retrieveSecureData("email_token_$userId")
                if (storedToken != token) {
                    return@withContext Result.Failure(Exception("Invalid verification token"))
                }

                // Update user
                val updatedUser = user.copy(isEmailVerified = true)
                userDao.updateUser(updatedUser)

                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Failure(e)
            }
        }

    suspend fun requestPasswordReset(email: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val user = userDao.getUserByEmail(email)
                    ?: return@withContext Result.Failure(Exception("User not found"))

                // Generate reset token
                val resetToken = securityManager.generateToken()
                securityManager.storeSecureData("reset_token_${user.id}", resetToken)

                // In production, send email with reset token
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Failure(e)
            }
        }
}