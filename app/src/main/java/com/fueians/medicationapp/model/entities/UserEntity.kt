package com.fueians.medicationapp.model.entities
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

/**
 * UserEntity
 *
 * Responsibility: Represent user data in the database.
 *
 * This entity stores core user information including authentication details,
 * profile data, and timestamps. It serves as the foundation for the user
 * management system in the medication management application.
 */
@Entity(tableName = "users")
data class UserEntity(
    /**
     * User ID - Primary key
     * Unique identifier for the user (typically from Supabase Auth)
     */
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    /**
     * User name
     * Display name of the user
     */
    @ColumnInfo(name = "name")
    val name: String,

    /**
     * Email address
     * User's email used for authentication and communication
     */
    @ColumnInfo(name = "email")
    val email: String,

    /**
     * Hashed password
     * Securely hashed password (encrypted by EncryptionService)
     */
    @ColumnInfo(name = "password_hash")
    val passwordHash: String,

    /**
     * Phone number (optional)
     * User's contact phone number
     */
    @ColumnInfo(name = "phone")
    val phone: String? = null,

    /**
     * Profile photo URL (optional)
     * URL to the user's profile photo (may be stored in Supabase Storage)
     */
    @ColumnInfo(name = "profile_photo_url")
    val profilePhotoUrl: String? = null,

    /**
     * Email verification status
     * Indicates whether the user has verified their email address
     */
    @ColumnInfo(name = "is_email_verified")
    val isEmailVerified: Boolean = false,

    /**
     * Account creation timestamp
     * Unix timestamp (milliseconds) when the account was created
     */
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    /**
     * Last update timestamp
     * Unix timestamp (milliseconds) of the last profile update
     */
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)