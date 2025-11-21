package com.fueians.medicationapp.presenter.TestRepo

import at.favre.lib.crypto.bcrypt.BCrypt
import com.fueians.medicationapp.model.dao.UserDao
import com.fueians.medicationapp.model.entities.UserEntity
import com.fueians.medicationapp.presenter.Login.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.UUID

class UserRepository(private val userDao: UserDao) {
    private val backgroundDispatcher = Dispatchers.IO

    suspend fun createAccount(name: String, email: String, password: String): Result<UserEntity> =
        withContext(backgroundDispatcher) {
            try {
                if (emailExists(email)) {
                    return@withContext Result.Failure(Exception("An account with this email already exists."))
                }
                val hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray())
                val newUser = UserEntity(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    email = email,
                    passwordHash = hashedPassword
                )
                userDao.insertUser(newUser)
                Result.Success(newUser)
            } catch (e: Exception) {
                Result.Failure(e)
            }
        }

    suspend fun login(email: String, password: String): Result<UserEntity> =
        withContext(backgroundDispatcher) {
            try {
                val user = userDao.getUserByEmail(email).firstOrNull()
                    ?: return@withContext Result.Failure(Exception("User not found."))
                val result = BCrypt.verifyer().verify(password.toCharArray(), user.passwordHash)
                if (result.verified) {
                    Result.Success(user)
                } else {
                    Result.Failure(Exception("Invalid password."))
                }
            } catch (e: Exception) {
                Result.Failure(e)
            }
        }

    private suspend fun emailExists(email: String): Boolean {
        return userDao.getUserByEmail(email).firstOrNull() != null
    }

    fun getUserByEmail(email: String): Flow<UserEntity?> {
        return userDao.getUserByEmail(email)
    }

    fun getUserById(userId: String): Flow<UserEntity?> {
        return userDao.getUserById(userId)
    }

    suspend fun updateUser(user: UserEntity) = withContext(backgroundDispatcher) {
        userDao.updateUser(user)
    }

    suspend fun changePassword(userId: String, oldPassword: String, newPassword: String) =
        withContext(backgroundDispatcher) {
            val user = userDao.getUserById(userId).firstOrNull()
                ?: throw Exception("User not found")

            val result = BCrypt.verifyer().verify(oldPassword.toCharArray(), user.passwordHash)
            if (!result.verified) {
                throw Exception("Old password is not correct")
            }

            val newHashedPassword = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray())
            val updatedUser = user.copy(passwordHash = newHashedPassword)
            userDao.updateUser(updatedUser)
        }

    suspend fun deleteUser(user: UserEntity) = withContext(backgroundDispatcher) {
        userDao.deleteUser(user)
    }
}
