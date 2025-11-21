package com.fueians.medicationapp.model.repository;

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.Observable
import com.fueians.medicationapp.model.dao.UserDao
import com.fueians.medicationapp.remote.SupabaseClient
import com.fueians.medicationapp.model.services.EncryptionService

// UserRepository: Handles all operations related to user data and authentication
class UserRepository (
    private val userDao: UserDao,
    private val supabaseClient: SupabaseClient,
    private val encryptionService: EncryptionService,
    private val securityManager: SecurityManager)
{
    // Register a new user
    fun registerUser(email: String, password: String, name: String): Completable {
    // Direct call, let Supabase handle errors
        val encryptedPassword = encryptionService.encrypt(password)
        return supabaseClient.register(email, encryptedPassword, name)
    }

    // Login user and save profile locally
    fun loginUser(email: String, password: String): Single<User> {
        val encryptedPassword = encryptionService.encrypt(password)
        return supabaseClient.login(email, encryptedPassword)
            .doOnSuccess { user ->
                userDao.saveUser(user)
                securityManager.setLoggedIn(true)
            }
    }
    // Verify email using received code
    fun verifyEmail(code: String): Completable {
        return supabaseClient.verifyEmail(code)
    }


    // Send password reset request
    fun resetPassword(email: String): Completable {
        return supabaseClient.resetPassword(email)
    }


    // Get user profile from local database (Observable)
    fun getUserProfile(): Observable<User> {
        return userDao.observeUser()
    }


    // Update user profile locally and remotely
    fun updateUserProfile(user: User): Completable {
        return supabaseClient.updateProfile(user)
            .doOnComplete { userDao.saveUser(user) }
    }


    // Change account password
    fun changePassword(oldPassword: String, newPassword: String): Completable {
        val oldEnc = encryptionService.encrypt(oldPassword)
        val newEnc = encryptionService.encrypt(newPassword)
        return supabaseClient.changePassword(oldEnc, newEnc)
    }


    // Logout: clear local data and update session state
    fun logout(): Completable {
        return Completable.fromAction {
            securityManager.setLoggedIn(false)
            userDao.clearUser()
        }
    }


    // Check if user is logged in
    fun isUserLoggedIn(): Boolean = securityManager.isLoggedIn()

}