package com.fueians.medicationapp.model.services

import com.fueians.medicationapp.model.entities.UserEntity
import com.fueians.medicationapp.model.security.PasswordHasher
import com.fueians.medicationapp.model.security.CryptoManager
import com.fueians.medicationapp.model.security.TokenManager

data class AuthResponse(
    val token: String,
    val user: UserEntity
)

class AuthService(
    private val passwordHasher: PasswordHasher,
    private val tokenManager: TokenManager,
    private val cryptoManager: CryptoManager
) {

    // New functions needed by AuthRepository
    suspend fun login(email: String, password: String): AuthResponse {
        val user = authenticateUser(email, password)
            ?: throw IllegalArgumentException("Invalid email or password")

        val token = tokenManager.generateToken(user.id, user.email)
        tokenManager.saveUserInfo(user)

        return AuthResponse(token, user)
    }

    suspend fun signup(email: String, password: String): AuthResponse {
        val user = registerUser(email, password)
        val token = tokenManager.generateToken(user.id, user.email)
        tokenManager.saveUserInfo(user)

        return AuthResponse(token, user)
    }

    suspend fun authenticateUser(email: String, password: String): UserEntity? {
        // Retrieve stored password hash from database
        val storedPasswordHash = getStoredPasswordHash(email) ?: return null

        // Verify password hash matches
        val isValid = passwordHasher.verify(password, storedPasswordHash)

        return if (isValid) {
            getUserByEmail(email)
        } else {
            null
        }
    }

    private suspend fun registerUser(email: String, password: String): UserEntity {
        // Hash the password before storing
        val hashedPassword = passwordHasher.hash(password)

        // Create and save user (this would interact with your database)
        val newUser = UserEntity(
            id = generateUserId(),
            email = email,

        )

        // Save user to database with hashed password
        saveUserToDatabase(newUser, hashedPassword)

        return newUser
    }

    fun logoutUser(userId: String) {
        // Clear user's token
        tokenManager.clearToken()

        // Additional cleanup logic (clear cache, etc.)
        clearUserSession(userId)
    }

    suspend fun refreshToken(token: String): String {
        // Validate the current token
        if (!tokenManager.validateToken(token)) {
            throw SecurityException("Invalid token")
        }

        // Refresh and return new token
        val newToken = tokenManager.refreshToken(token)
        tokenManager.saveToken(newToken)

        return newToken
    }

    fun validateUserCredentials(email: String, passwordHash: String): Boolean {
        // Retrieve stored password hash from database
        val storedPasswordHash = getStoredPasswordHash(email) ?: return false

        // Verify password hash matches
        return passwordHasher.verify(passwordHash, storedPasswordHash)
    }

    // Private helper methods
    private fun generateUserId(): String {
        return java.util.UUID.randomUUID().toString()
    }

    private suspend fun getUserByEmail(email: String): UserEntity? {
        // TODO: Database query implementation
        // Example: return userDao.getUserByEmail(email)
        return null // Replace with actual database query
    }

    private fun saveUserToDatabase(user: UserEntity, passwordHash: String) {
        // TODO: Database save implementation
        // Example: userDao.insert(user, passwordHash)
    }

    private fun getStoredPasswordHash(email: String): String? {
        // TODO: Database query implementation
        // Example: return userDao.getPasswordHashByEmail(email)
        return null // Replace with actual database query
    }

    private fun clearUserSession(userId: String) {
        // Clear session data
        // TODO: Clear any cached data for this user
    }
}