package com.fueians.medicationapp.model.repository

import at.favre.lib.crypto.bcrypt.BCrypt
import com.fueians.medicationapp.model.dao.UserDao
import com.fueians.medicationapp.model.entities.UserEntity
import java.util.UUID

/**
 * AuthRepository
 *
 * Responsibility: Provide a clean, synchronous API for user authentication operations.
 * All methods in this repository perform blocking I/O and MUST be called from a background thread.
 */
class AuthRepository {

    // DAO is now a private attribute with a placeholder implementation.
    private val userDao: UserDao = object : UserDao {
        private val inMemoryUsers = mutableMapOf<String, UserEntity>()

        override fun getUserById(id: String): UserEntity? = inMemoryUsers[id]
        override fun getUserByEmail(email: String): UserEntity? = inMemoryUsers.values.find { it.email == email }
        override fun insertUser(user: UserEntity) { inMemoryUsers[user.id] = user }
        override fun updateUser(user: UserEntity) { inMemoryUsers[user.id] = user }
        override fun deleteUser(user: UserEntity) { inMemoryUsers.remove(user.id) }
    }

    fun createAccount(name: String, email: String, password: String): UserEntity {
        if (userDao.getUserByEmail(email) != null) {
            throw Exception("An account with this email already exists.")
        }
        val hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray())
        val newUser = UserEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            email = email,
            passwordHash = hashedPassword
        )
        userDao.insertUser(newUser)
        return newUser
    }

    fun login(email: String, password: String): UserEntity {
        val user = userDao.getUserByEmail(email)
            ?: throw Exception("User not found.")

        val result = BCrypt.verifyer().verify(password.toCharArray(), user.passwordHash)
        if (result.verified) {
            return user
        } else {
            throw Exception("Invalid password.")
        }
    }

    fun changePassword(userId: String, oldPassword: String, newPassword: String) {
        val user = userDao.getUserById(userId)
            ?: throw Exception("User not found")

        val result = BCrypt.verifyer().verify(oldPassword.toCharArray(), user.passwordHash)
        if (!result.verified) {
            throw Exception("Old password is not correct")
        }

        val newHashedPassword = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray())
        val updatedUser = user.copy(passwordHash = newHashedPassword)
        userDao.updateUser(updatedUser)
    }
}
