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

    // ========== INSERT ==========
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReports(reports: List<ReportEntity>)

    // ========== UPDATE ==========
    @Update
    suspend fun updateReport(report: ReportEntity)

    // ========== DELETE ==========
    @Delete
    suspend fun deleteReport(report: ReportEntity)

    @Query("DELETE FROM reports WHERE id = :id")
    suspend fun deleteReportById(id: String)

    // ========== QUERY - FLOW (Reactive) ==========
    @Query("SELECT * FROM reports WHERE user_id = :userId ORDER BY created_at DESC")
    fun getReportsByUser(userId: String): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports WHERE user_id = :userId ORDER BY created_at DESC LIMIT :limit")
    fun getRecentReports(userId: String, limit: Int): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports WHERE user_id = :userId AND report_type = :reportType ORDER BY created_at DESC")
    fun getReportsByType(userId: String, reportType: String): Flow<List<ReportEntity>>

    @Query("""
        SELECT * FROM reports 
        WHERE user_id = :userId 
        AND start_date >= :startDate 
        AND end_date <= :endDate 
        ORDER BY created_at DESC
    """)
    fun getReportsByDateRange(userId: String, startDate: Long, endDate: Long): Flow<List<ReportEntity>>

    // ========== QUERY - SYNC (Non-reactive) ==========
    @Query("SELECT * FROM reports WHERE id = :id")
    suspend fun getReportById(id: String): ReportEntity?

    @Query("SELECT * FROM reports ORDER BY created_at DESC")
    suspend fun getAllReportsSync(): List<ReportEntity>

    @Query("SELECT * FROM reports WHERE user_id = :userId ORDER BY created_at DESC")
    suspend fun getReportsByUserSync(userId: String): List<ReportEntity>

    @Query("SELECT * FROM reports WHERE user_id = :userId ORDER BY created_at DESC LIMIT 1")
    suspend fun getLatestReport(userId: String): ReportEntity?

    // ========== STATISTICS ==========
    @Query("SELECT COUNT(*) FROM reports WHERE user_id = :userId")
    suspend fun getReportCount(userId: String): Int

    @Query("SELECT AVG(adherence_rate) FROM reports WHERE user_id = :userId")
    suspend fun getAverageAdherenceRate(userId: String): Float?
}
