package com.fueians.medicationapp.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.UUID

@Entity(tableName = "drug_interactions")
data class DrugInteractionEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "drug1_id")
    val drug1Id: String,

    @ColumnInfo(name = "drug2_id")
    val drug2Id: String,

    @ColumnInfo(name = "drug1_name")
    val drug1Name: String,

    @ColumnInfo(name = "drug2_name")
    val drug2Name: String,

    val severity: String, // e.g., "minor", "moderate", "major"

    val description: String,

    // Again, effects would likely need a TypeConverter
    val effects: String? = null,

    val recommendations: String,

    @ColumnInfo(name = "cached_at")
    val cachedAt: Instant = Instant.now(),
)