package com.fueians.medicationapp.model.services

import com.fueians.medicationapp.model.entities.UserEntity
import com.fueians.medicationapp.model.remote.SupabaseClient // ⚠️ NEW IMPORT
import com.fueians.medicationapp.model.repository.UserRepository
import com.fueians.medicationapp.security.PasswordHasher
import com.fueians.medicationapp.security.TokenManager
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class AuthService(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher, // Still needed for local-only scenarios, but less for Supabase auth
    private val tokenManager: TokenManager,
    private val supabaseClient: SupabaseClient // ⚠️ NEW DEPENDENCY
) {
    // We'll primarily use the I/O scheduler for network calls.
    private val ioScheduler = Schedulers.io()

    // --- Authentication and Registration ---

    fun registerUser(name: String, email: String, password: String): Completable {
        // 1. Use Supabase to handle remote registration and hashing
        return supabaseClient.signUp(email, password, name)
            .flatMapCompletable { remoteUser ->
                // 2. If Supabase succeeds, save the user entity (including ID from Supabase) locally
                userRepository.saveUser(remoteUser)
            }
            .subscribeOn(ioScheduler) // Network work on I/O thread
    }

    fun login(email: String, password: String): Single<UserEntity> {
        // 1. Use Supabase to handle remote authentication
        return supabaseClient.signIn(email, password)
            .flatMap { remoteUser ->
                // 2. On success, save/update user details locally
                userRepository.saveUser(remoteUser) // saveUser returns Completable
                    .andThen(Single.just(remoteUser)) // Return the user after saving
            }
            .doOnSuccess { user ->
                // 3. Handle token management
                val authToken = generateAuthToken(user) // Or retrieve token from Supabase response
                tokenManager.saveAuthToken(authToken)
            }
            .subscribeOn(ioScheduler) // Network work on I/O thread
    }

    // ... logout and generateAuthToken methods remain the same ...
}