package com.fueians.medicationapp.model.repository

import com.fueians.medicationapp.model.entities.UserEntity
import com.fueians.medicationapp.model.services.AuthService
import com.fueians.medicationapp.model.security.TokenManager

class AuthRepository(
    private val authService: AuthService,
    private val tokenManager: TokenManager
) {

    suspend fun login(email: String, password: String): Result<UserEntity> {
        return try {
            val response = authService.login(email, password)
            tokenManager.saveToken(response.token)
            Result.Success(response.user)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    suspend fun signup(email: String, password: String): Result<UserEntity> {
        return try {
            val response = authService.signup(email, password)
            tokenManager.saveToken(response.token)
            Result.Success(response.user)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    fun logout() {
        tokenManager.clearToken()
    }

    fun getCurrentUser(): UserEntity? {
        return tokenManager.getCurrentUser()
    }

    fun isUserLoggedIn(): Boolean {
        return tokenManager.hasValidToken()
    }
}

// Result sealed class for handling success/error states
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Failure(val exception: Exception) : Result<Nothing>()
}