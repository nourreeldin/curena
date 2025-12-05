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

    // ========== INSERT ==========
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    // ========== UPDATE ==========
    @Update
    suspend fun updateUser(user: UserEntity)

    // ========== DELETE ==========
    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: String)

    // ========== QUERY - FLOW (Reactive) ==========
    @Query("SELECT * FROM users ORDER BY created_at DESC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserByIdFlow(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE email = :email")
    fun getUserByEmailFlow(email: String): Flow<UserEntity?>

    // ========== QUERY - SYNC (Non-reactive) ==========
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY created_at DESC")
    suspend fun getAllUsers(): List<UserEntity>

    @Query("SELECT * FROM users WHERE is_email_verified = 1")
    suspend fun getVerifiedUsers(): List<UserEntity>

    @Query("SELECT * FROM users WHERE is_email_verified = 0")
    suspend fun getUnverifiedUsers(): List<UserEntity>

    @Query("SELECT * FROM users WHERE name LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%'")
    suspend fun searchUsers(query: String): List<UserEntity>

    // ========== STATISTICS ==========
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Query("SELECT COUNT(*) FROM users WHERE is_email_verified = 1")
    suspend fun getVerifiedUserCount(): Int

    // ========== UTILITY ==========
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)")
    suspend fun isEmailRegistered(email: String): Boolean
}