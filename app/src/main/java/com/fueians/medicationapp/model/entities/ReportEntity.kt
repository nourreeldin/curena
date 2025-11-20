package com.fueians.medicationapp.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.UUID

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "report_type")
    val reportType: String,

    val title: String,

    @ColumnInfo(name = "start_date")
    val startDate: Long, // Timestamp

    @ColumnInfo(name = "end_date")
    val endDate: Long, // Timestamp

    @ColumnInfo(name = "adherence_rate")
    val adherenceRate: Float,

    @ColumnInfo(name = "total_medications")
    val totalMedications: Int,

    // Report data is likely stored as JSON string
    @ColumnInfo(name = "report_data")
    val reportData: String,

    @ColumnInfo(name = "file_path")
    val filePath: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Instant.now(),
)