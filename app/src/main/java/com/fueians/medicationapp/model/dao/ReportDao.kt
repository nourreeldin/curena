package com.fueians.medicationapp.model.dao
import androidx.room.*
import com.fueians.medicationapp.model.entities.ReportEntity
import kotlinx.coroutines.flow.Flow

/**
 * ReportDao
 *
 * Responsibility: Provide database access methods for reports.
 *
 * This DAO interface defines all database operations for ReportEntity,
 * enabling queries for report management and retrieval. Uses Flow for
 * reactive data updates.
 *
 * Related Classes: ReportEntity, AppDatabase
 */
@Dao
interface ReportDao {

    /**
     * Get all reports
     * Returns reports ordered by creation date descending (most recent first)
     */
    @Query("SELECT * FROM reports ORDER BY created_at DESC")
    suspend fun getAllReports(): Flow<List<ReportEntity>>

    /**
     * Get report by ID
     * Returns a Flow that emits the report or null
     *
     * @param id Report ID
     */
    @Query("SELECT * FROM reports WHERE id = :id")
    suspend fun getReportById(id: String): Flow<ReportEntity?>

    /**
     * Insert a report
     *
     * @param report Report to insert
     */
    @Insert
    suspend fun insertReport(report: ReportEntity)

    /**
     * Delete a report
     *
     * @param report Report to delete
     */
    @Delete
    suspend fun deleteReport(report: ReportEntity)
}