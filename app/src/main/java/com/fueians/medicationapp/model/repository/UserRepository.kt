package com.fueians.medicationapp.model.repository

import at.favre.lib.crypto.bcrypt.BCrypt
import com.fueians.medicationapp.model.dao.UserDao
import com.fueians.medicationapp.model.entities.UserEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.UUID

/**
 * UserRepository
 *
 * Responsibility: Provide a clean, RxJava-based API for user data operations.
 * This repository creates its own DAO dependency and ensures all database
 * operations are executed on a background thread.
 */
class UserRepository {

    // DAO is now a private attribute with a placeholder implementation.
    private val userDao: UserDao = object : UserDao {
        private val inMemoryUsers = mutableMapOf<String, UserEntity>()
        override fun getUserById(id: String): Flowable<UserEntity> = Flowable.fromIterable(inMemoryUsers.values.filter { it.id == id })
        override fun getUserByEmail(email: String): Single<UserEntity> {
            val user = inMemoryUsers.values.find { it.email == email }
            return if (user != null) Single.just(user) else Single.error(androidx.room.EmptyResultSetException("Query returned no rows"))
        }
        override fun insertUser(user: UserEntity): Completable = Completable.fromAction { inMemoryUsers[user.id] = user }
        override fun updateUser(user: UserEntity): Completable = Completable.fromAction { inMemoryUsers[user.id] = user }
        override fun deleteUser(user: UserEntity): Completable = Completable.fromAction { inMemoryUsers.remove(user.id) }
    }

    private val backgroundScheduler = Schedulers.io()

    fun createAccount(name: String, email: String, password: String): Single<UserEntity> {
        return userDao.getUserByEmail(email)
            .flatMapSingle<UserEntity> { Single.error(Exception("An account with this email already exists.")) }
            .onErrorResumeNext { error ->
                if (error is androidx.room.EmptyResultSetException) {
                    Single.fromCallable {
                        val hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray())
                        UserEntity(
                            id = UUID.randomUUID().toString(),
                            name = name,
                            email = email,
                            passwordHash = hashedPassword
                        )
                    }.flatMap { newUser ->
                        userDao.insertUser(newUser).toSingleDefault(newUser)
                    }
                } else {
                    Single.error(error)
                }
            }
            .subscribeOn(backgroundScheduler)
    }

    fun login(email: String, password: String): Single<UserEntity> {
        return userDao.getUserByEmail(email)
            .flatMap { user ->
                val result = BCrypt.verifyer().verify(password.toCharArray(), user.passwordHash)
                if (result.verified) Single.just(user) else Single.error(Exception("Invalid password."))
            }
            .subscribeOn(backgroundScheduler)
    }

    fun getUserById(userId: String): Flowable<UserEntity> {
        return userDao.getUserById(userId).subscribeOn(backgroundScheduler)
    }

    fun updateUser(user: UserEntity): Completable {
        return userDao.updateUser(user).subscribeOn(backgroundScheduler)
    }

    fun changePassword(userId: String, oldPassword: String, newPassword: String): Completable {
        return userDao.getUserById(userId).firstOrError()
            .flatMapCompletable { user ->
                if (!BCrypt.verifyer().verify(oldPassword.toCharArray(), user.passwordHash).verified) {
                    return@flatMapCompletable Completable.error(Exception("Old password is not correct"))
                }
                val newHashedPassword = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray())
                userDao.updateUser(user.copy(passwordHash = newHashedPassword))
            }
            .subscribeOn(backgroundScheduler)
    }

    fun deleteUser(user: UserEntity): Completable {
        return userDao.deleteUser(user).subscribeOn(backgroundScheduler)
    }
}
