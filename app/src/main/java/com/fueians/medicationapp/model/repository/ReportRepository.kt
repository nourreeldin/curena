package com.fueians.medicationapp.model.repository

import com.fueians.medicationapp.model.dao.AdherenceLogDao
import com.fueians.medicationapp.model.dao.ReportDao
import com.fueians.medicationapp.model.entities.AdherenceLog
import com.fueians.medicationapp.model.entities.DateRange
import com.fueians.medicationapp.model.entities.ReportEntity
import com.fueians.medicationapp.model.entities.ReportType
import java.util.Date
import java.util.UUID

/**
 * ReportRepository
 *
 * Responsibility: Provide a clean, synchronous API for generating and managing reports.
 * All methods in this repository perform blocking I/O and MUST be called from a background thread.
 */
class ReportRepository {

    // DAOs are now private attributes with placeholder implementations.
    private val reportDao: ReportDao = object : ReportDao {
        private val reports = mutableMapOf<String, ReportEntity>()
        override fun getReportsForPatient(patientId: String) = reports.values.filter { it.patientId == patientId }
        override fun getReportById(reportId: String) = reports[reportId]
        override fun insertReport(report: ReportEntity) { reports[report.id] = report }
        override fun deleteReport(reportId: String) { reports.remove(reportId) }
    }

    private val adherenceLogDao: AdherenceLogDao = object : AdherenceLogDao {
        override fun getLogsForPatient(patientId: String): List<AdherenceLog> = emptyList()
        override fun insertLog(log: AdherenceLog) {}
        override fun getMissedDoses(): List<AdherenceLog> = emptyList()
    }

    /**
     * Generates a new report based on the given parameters.
     * This is a blocking method and must be called from a background thread.
     */
    fun generateReport(patientId: String, type: ReportType, dateRange: DateRange): ReportEntity {
        // In a real app, this would query various DAOs and compile data.
        val reportDataJson = when (type) {
            ReportType.ADHERENCE_SUMMARY -> "{\"adherence\": \"92%\"}"
            ReportType.MEDICATION_HISTORY -> "{\"medications\": [\"Aspirin\", \"Lisinopril\"]}"
            ReportType.FULL_REPORT -> "{\"adherence\": \"92%\", \"medications\": [\"Aspirin\"]}"
        }

        val report = ReportEntity(
            id = UUID.randomUUID().toString(),
            patientId = patientId,
            generationDate = Date(),
            startDate = dateRange.startDate,
            endDate = dateRange.endDate,
            type = type,
            dataJson = reportDataJson
        )

        reportDao.insertReport(report)
        return report
    }

    /**
     * Loads all previously generated reports for a specific patient.
     * This is a blocking method and must be called from a background thread.
     */
    fun loadReports(patientId: String): List<ReportEntity> {
        return reportDao.getReportsForPatient(patientId)
    }

    /**
     * Deletes a specific report.
     * This is a blocking method and must be called from a background thread.
     */
    fun deleteReport(reportId: String) {
        reportDao.deleteReport(reportId)
    }

    /**
     * Shares a report. (e.g., creates a PDF, sends an email)
     * This is a blocking method and must be called from a background thread.
     */
    fun shareReport(reportId: String) {
        val report = reportDao.getReportById(reportId)
            ?: throw Exception("Report not found.")

        // Placeholder for sharing logic (e.g., creating a file, using an Intent)
        println("Sharing report: ${report.id} of type ${report.type}")
    }
}
