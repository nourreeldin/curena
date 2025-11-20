package com.fueians.medicationapp.model.dao

import androidx.room.*
import com.fueians.medicationapp.model.entities.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * UserDao
 *
 * Responsibility: Provide database access methods for user data.
 *
 * This DAO interface defines all database operations for UserEntity,
 * including CRUD operations and user queries. Uses Flow for reactive
 * data updates.
 *
 * Related Classes: UserEntity, AppDatabase
 */
@Dao
interface UserDao {

    /**
     * Get user by ID
     * Returns a Flow that emits the user or null
     *
     * @param id User ID
     */
    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: String): Flow<UserEntity?>

    /**
     * Get user by email
     * Returns a Flow that emits the user or null
     *
     * @param email User email address
     */
    @Query("SELECT * FROM users WHERE email = :email")
    fun getUserByEmail(email: String): Flow<UserEntity?>

    /**
     * Insert a user
     *
     * @param user User to insert
     */
    @Insert
    suspend fun insertUser(user: UserEntity)

    /**
     * Update a user
     *
     * @param user User to update
     */
    @Update
    suspend fun updateUser(user: UserEntity)

    /**
     * Delete a user
     *
     * @param user User to delete
     */
    @Delete
    suspend fun deleteUser(user: UserEntity)
}