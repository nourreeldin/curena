package com.fueians.medicationapp.view.interfaces

import com.fueians.medicationapp.model.entities.ReportEntity

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