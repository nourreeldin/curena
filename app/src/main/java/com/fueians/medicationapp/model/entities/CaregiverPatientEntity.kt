package com.fueians.medicationapp.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.UUID

@Entity(tableName = "caregiver_patient")
data class CaregiverPatientEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "caregiver_id")
    val caregiverId: String,

    @ColumnInfo(name = "patient_id")
    val patientId: String,

    @ColumnInfo(name = "relationship_type")
    val relationshipType: String,

    @ColumnInfo(name = "permission_level")
    val permissionLevel: String,

    @ColumnInfo(name = "invitation_status")
    val invitationStatus: String, // e.g., "pending", "accepted"

    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Instant.now(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Instant = Instant.now(),
)