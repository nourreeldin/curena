package com.fueians.medicationapp.presenter.Report

import com.fueians.medicationapp.model.repository.DateRange
import com.fueians.medicationapp.model.repository.ReportEntity
import com.fueians.medicationapp.model.repository.ReportRepository
import com.fueians.medicationapp.model.repository.ReportType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.Date

// =========================================================================
// 1. View Interface
// =========================================================================

/**
 * Interface for the Report View (e.g., Activity or Fragment) to display data and feedback.
 */
interface IReportView {
    fun showLoading()
    fun hideLoading()
    fun showReports(reports: List<ReportEntity>)
    fun showReportDetails(report: ReportEntity)
    fun onReportGenerated(report: ReportEntity)
    fun onReportDeleted(reportId: String)
    fun onReportExported(pdfPath: String)
    fun displayAdherenceRate(rate: Float)
    fun displayAdherenceStatistics(stats: Map<String, Any>)
    fun showErrorMessage(message: String)
}

// =========================================================================
// 2. Report Generation Service Placeholder (Needed for Presenter injection)
// =========================================================================

/**
 * Placeholder service used only for dependency injection in the Presenter.
 * Actual logic is in ReportRepository and its internal service.
 */
class ReportGenerationService

// =========================================================================
// 3. Presenter
// =========================================================================

/**
 * Handles report generation, data processing, and user interactions for reporting views.
 *
 * Responsibility: Handle report generation, process adherence data, create statistics and visualizations.
 * Usage: Used by report activities to generate and display reports.
 */
class ReportPresenter(
    private var view: IReportView?
) {
    private val reportRepository: ReportRepository = ReportRepository(
        reportDao = TODO(),
        adherenceLogDao = TODO(),
        reportGenerationService = TODO(),
        backgroundDispatcher = TODO()
    )
    // reportGenerationService is included here for dependency graph completeness but not directly used by presenter methods
    private val reportGenerationService: ReportGenerationService = ReportGenerationServie()
    // compositeDisposable is not needed when using Coroutines/Flows.
    private val presenterScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // --- Core Methods ---

    /**
     * Attach view when the Activity/Fragment is created.
     */
    fun attachView(view: IReportView) {
        this.view = view
    }

    /**
     * Detach view when the Activity/Fragment is destroyed to prevent memory leaks.
     */
    fun detachView() {
        this.view = null
        presenterScope.cancel() // Cancel all coroutines launched in this scope
    }

    // --- Data Loading ---

    /**
     * Load a stream of all reports for the patient.
     * @param patientId The ID of the patient whose reports are being loaded.
     */
    fun loadReports(patientId: String) {
        presenterScope.launch {
            reportRepository.loadReports(patientId)
                .onStart { view?.showLoading() }
                .onCompletion { view?.hideLoading() }
                .catch { exception ->
                    view?.showErrorMessage("Failed to load reports: ${exception.message}")
                }
                .collect { reports ->
                    view?.showReports(reports)
                }
        }
    }

    /**
     * Load detailed information for a specific report.
     */
    fun loadReportDetails(reportId: String) {
        presenterScope.launch {
            reportRepository.loadReportDetails(reportId)
                .onStart { view?.showLoading() }
                .onCompletion { view?.hideLoading() }
                .catch { exception ->
                    view?.showErrorMessage("Failed to load report details: ${exception.message}")
                }
                .collect { report ->
                    if (report != null) {
                        view?.showReportDetails(report)
                    } else {
                        view?.showErrorMessage("Report details not found.")
                    }
                }
        }
    }

    // --- Actions ---

    /**
     * Generate a new report based on the provided parameters.
     */
    fun generateReport(patientId: String, startDate: Date, endDate: Date, type: ReportType) {
        presenterScope.launch {
            view?.showLoading()
            try {
                val dateRange = DateRange(startDate, endDate)
                val newReport = reportRepository.generateReport(patientId, dateRange, type)
                view?.onReportGenerated(newReport)
            } catch (e: Exception) {
                view?.showErrorMessage("Error generating report: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }

    /**
     * Delete a report by ID.
     */
    fun deleteReport(reportId: String) {
        presenterScope.launch {
            view?.showLoading()
            try {
                reportRepository.deleteReport(reportId)
                view?.onReportDeleted(reportId)
            } catch (e: Exception) {
                view?.showErrorMessage("Error deleting report: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }

    /**
     * Exports a saved report to a PDF file.
     */
    fun exportReportToPdf(reportId: String) {
        presenterScope.launch {
            view?.showLoading()
            try {
                val pdfPath = reportRepository.exportReportToPdf(reportId)
                view?.onReportExported(pdfPath)
            } catch (e: Exception) {
                view?.showErrorMessage("Error exporting report: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }

    // --- Calculations ---

    /**
     * Calculate the patient's adherence rate over a specific date range.
     */
    fun calculateAdherenceRate(patientId: String, dateRange: DateRange) {
        presenterScope.launch {
            view?.showLoading()
            try {
                val rate = reportRepository.calculateAdherenceRate(patientId, dateRange)
                view?.displayAdherenceRate(rate)
            } catch (e: Exception) {
                view?.showErrorMessage("Error calculating adherence rate: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }

    /**
     * Get statistics (e.g., total doses, adherence rate) for a specific report.
     */
    fun getAdherenceStatistics(reportId: String) {
        presenterScope.launch {
            view?.showLoading()
            try {
                val stats = reportRepository.getAdherenceStatistics(reportId)
                view?.displayAdherenceStatistics(stats)
            } catch (e: Exception) {
                view?.showErrorMessage("Error fetching statistics: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }
}