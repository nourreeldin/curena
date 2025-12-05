package com.fueians.medicationapp.presenter.Report

import android.content.Context
import com.fueians.medicationapp.model.entities.ReportEntity
import com.fueians.medicationapp.model.entities.ReportType
import com.fueians.medicationapp.model.repository.ReportRepository
import com.fueians.medicationapp.view.interfaces.IReportView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.Date

class ReportPresenter(
    private var view: IReportView?,
    context: Context
) {

    private val reportRepository = ReportRepository(context)
    private val presenterScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Attach/detach view
    fun attachView(view: IReportView) {
        this.view = view
    }

    fun detachView() {
        view = null
        presenterScope.cancel()
    }

    // Load all reports for a patient
    fun loadReports(patientId: String) {
        presenterScope.launch {
            reportRepository.getAllReports(patientId)
                .onStart { view?.showLoading() }
                .onCompletion { view?.hideLoading() }
                .catch { e -> view?.showErrorMessage("Failed to load reports: ${e.message}") }
                .collect { reports -> view?.showReports(reports) }
        }
    }

    // Load specific report details
    fun loadReportDetails(reportId: String) {
        presenterScope.launch {
            try {
                view?.showLoading()
                val report: ReportEntity? = reportRepository.getReportById(reportId)
                view?.hideLoading()
                if (report != null) {
                    view?.showReportDetails(report)
                } else {
                    view?.showErrorMessage("Report details not found.")
                }
            } catch (e: Exception) {
                view?.hideLoading()
                view?.showErrorMessage("Failed to load report details: ${e.message}")
            }
        }
    }

    // Generate new report
    fun generateReport(patientId: String, startDate: Date, endDate: Date, type: ReportType) {
        presenterScope.launch {
            view?.showLoading()
            try {
                val newReport = reportRepository.generateReport(
                    patientId,
                    startDate.time,
                    endDate.time,
                    type
                )
                view?.onReportGenerated(newReport)
            } catch (e: Exception) {
                view?.showErrorMessage("Error generating report: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }

    // Delete a report
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

    // Export report to PDF
    fun exportReportToPdf(reportId: String) {
        presenterScope.launch {
            view?.showLoading()
            try {
                val pdfPath = reportRepository.exportReportToPDF(reportId)
                if (pdfPath != null) {
                    view?.onReportExported(pdfPath)
                } else {
                    view?.showErrorMessage("Failed to export report: file path is null")
                }
            } catch (e: Exception) {
                view?.showErrorMessage("Error exporting report: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }
}
