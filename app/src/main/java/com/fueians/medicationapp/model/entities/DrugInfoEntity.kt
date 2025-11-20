package com.fueians.medicationapp.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.UUID

@Entity(tableName = "drug_info")
data class DrugInfoEntity(
    @PrimaryKey
    val id: String, // Likely the drug ID from the external API

    val name: String,

    @ColumnInfo(name = "generic_name")
    val genericName: String? = null,

    // Note: Brand names, side effects, warnings would likely need a TypeConverter
    // to store List<String> as a single string (e.g., JSON or comma-separated)
    // For simplicity, we define them here as strings:
    @ColumnInfo(name = "brand_names")
    val brandNames: String? = null,

    val description: String,

    @ColumnInfo(name = "side_effects")
    val sideEffects: String? = null,

    @ColumnInfo(name = "warnings")
    val warnings: String? = null,

    @ColumnInfo(name = "cached_at")
    val cachedAt: Instant = Instant.now(),
)