package com.fueians.medicationapp.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.UUID

/**
 * UserEntity represents a user in the local database.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val name: String,
    val email: String,

    @ColumnInfo(name = "password_hash")
    val passwordHash: String,

    @ColumnInfo(name = "photo_url")
    val photoUrl: String? = null, // Added for profile photo

    @ColumnInfo(name = "creation_time")
    val creationTime: Instant = Instant.now(),

    @ColumnInfo(name = "last_login")
    var lastLogin: Instant? = null
)
