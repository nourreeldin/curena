package com.fueians.medicationapp.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.Date
import java.util.UUID

/**
 * UserEntity represents the structure of a user record within the application.
 *
 * Notes for developers:
 * - Follow consistent naming conventions that match the Supabase schema.
 * - Do not store plain-text passwords — use hashed or encrypted values.
 * - This class will use ORM (Room) annotations to map fields to database columns.
 */

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity (
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "email")
    var email: String = "",

    @ColumnInfo(name = "hashed_password")
    var passwordHash: String = "",

    @ColumnInfo(name = "creation_time")
    val creationTime: Instant = Instant.now(),

    @ColumnInfo("last_login")
    var lastLogin: Instant? = null,
) {
}