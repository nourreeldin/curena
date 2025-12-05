package com.fueians.medicationapp.model.repository

import android.content.Context
import com.fueians.medicationapp.model.dao.ReportDao
import com.fueians.medicationapp.model.dao.AdherenceLogDao
import com.fueians.medicationapp.model.entities.ReportEntity
import com.fueians.medicationapp.model.entities.AdherenceLogEntity
import com.fueians.medicationapp.model.entities.ReportType
import com.fueians.medicationapp.model.database.AppDatabase
import com.fueians.medicationapp.model.services.ReportGenerationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class ReportRepository(private val context: Context) {

    private val reportDao: ReportDao by lazy {
        AppDatabase.getInstance(context).reportDao()
    }
    private val adherenceLogDao: AdherenceLogDao by lazy {
        AppDatabase.getInstance(context).adherenceLogDao()
    }
    private val reportGenerationService: ReportGenerationService by lazy {
        ReportGenerationService(context)
    }

    /**
     * Get all reports for user
     */
    fun getAllReports(userId: String): Flow<List<ReportEntity>> {
        return reportDao.getReportsByUser(userId)
    }

    /**
     * Get report by ID
     */
    suspend fun getReportById(id: String): ReportEntity? = withContext(Dispatchers.IO) {
        reportDao.getReportById(id)
    }

    /**
     * Generate and save new report
     */
    suspend fun generateReport(
        userId: String,
        startDate: Long,
        endDate: Long,
        reportType: ReportType
    ): ReportEntity = withContext(Dispatchers.IO) {

        // 1. Fetch adherence logs in range
        val logs: List<AdherenceLogEntity> =
            adherenceLogDao.getLogsByDateRange(userId, startDate, endDate).first()

        // 2. Count medications taken in this period
        val totalMedications = logs.map { it.medicationId }.distinct().size

        // 3. Convert timestamps to Date objects
        val startDateObj = Date(startDate)
        val endDateObj = Date(endDate)

        // 4. Generate report entity using the service
        val report = reportGenerationService.generateReport(
            startDate = startDateObj,
            endDate = endDateObj,
            type = reportType,
            logs = logs,
            totalMedications = totalMedications
        ).copy(
            userId = userId   // override placeholder userId inside service
        )

        // 5. Save into Room
        reportDao.insertReport(report)

        return@withContext report
    }


    /**
     * Delete report
     */
    suspend fun deleteReport(id: String) = withContext(Dispatchers.IO) {
        val report = reportDao.getReportById(id)
        if (report != null) {
            reportDao.deleteReport(report)
        }
    }

    /**
     * Get adherence logs for date range
     */
    fun getAdherenceLogs(userId: String, startDate: Long, endDate: Long): Flow<List<AdherenceLogEntity>> {
        return adherenceLogDao.getLogsByDateRange(userId, startDate, endDate)
    }

    /**
     * Export report to PDF
     */
    suspend fun exportReportToPDF(reportId: String): String? = withContext(Dispatchers.IO) {
        val report = reportDao.getReportById(reportId) ?: return@withContext null

        // Create PDF output file path
        val outputFile = File(
            context.getExternalFilesDir(null),
            "Report_${report.id}.pdf"
        )

        // Generate PDF and save it to the file path
        reportGenerationService.exportToPdf(report, outputFile.absolutePath)

        // Return absolute file path
        return@withContext outputFile.absolutePath
    }


    /**
     * Get recent reports
     */
    fun getRecentReports(userId: String, limit: Int = 5): Flow<List<ReportEntity>> {
        return reportDao.getRecentReports(userId, limit)
    }
}