package com.fueians.medicationapp.model.repository



import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import com.fueians.medicationapp.model.dao.ReportDao
import com.fueians.medicationapp.model.dao.AdherenceLogDao
import com.fueians.medicationapp.model.dao.MedicationDaoremote.SupabaseClient

class ReportRepository
    (
    private val reportDao: ReportDao,
    private val adherenceLogDao: AdherenceLogDao,
    private val supabaseClient: SupabaseClient,
    private val reportGenerationService: ReportGenerationService
)
{


    // Get all reports stored locally
    fun getAllReports(): Observable<List<Report>> {
        return reportDao.getAllReports()
    }


    // Get a specific report by ID
    fun getReportById(id: String): Observable<Report> {
        return reportDao.getReportById(id)
    }


    // Create a new report using logs and generation service
    fun createReport(startDate: Date, endDate: Date, type: ReportType): Single<Report> {
        return ReportGenerationService.generateReport(startDate, endDate, type)
            .doOnSuccess { report ->
                reportDao.saveReport(report)
            }
    }


    // Delete report locally
    fun deleteReport(id: String): Completable {
        return Completable.fromAction {
            reportDao.deleteReport(id)
        }
    }


    // Retrieve adherence logs between date range
    fun getAdherenceLogs(startDate: Date, endDate: Date): Observable<List<AdherenceLog>> {
        return adherenceLogDao.getLogs(startDate, endDate)
    }


    // Calculate statistics for a report
    fun calculateStatistics(reportId: String): Single<Map<String, Any>> {
        return ReportGenerationService.calculateStatistics(reportId)
    }


    // Sync reports with server
    fun syncReports(): Completable {
        return supabaseClient.syncReports()
            .andThen(reportDao.getAllReports().firstOrError())
            .flatMapCompletable { reports ->
                Completable.fromAction {
                    reportDao.saveReports(reports)
                }
            }
    }
}

fun ReportGenerationService.Companion.generateReport(startDate: Any, endDate: Any, type: Any) {}
