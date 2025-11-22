package com.fueians.medicationapp.presenter.Report

import com.fueians.medicationapp.model.entities.DateRange
import com.fueians.medicationapp.model.entities.ReportEntity
import com.fueians.medicationapp.model.entities.ReportType
import com.fueians.medicationapp.model.repository.ReportRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

interface IReportView {
    fun showLoading()
    fun hideLoading()
    fun displayReports(reports: List<ReportEntity>)
    fun onReportGenerated(report: ReportEntity)
    fun onReportDeleted()
    fun onReportShared()
    fun displayError(message: String)
}

class ReportPresenter(private var view: IReportView?) {

    private val reportRepository = ReportRepository()
    private val compositeDisposable = CompositeDisposable()

    fun attachView(view: IReportView) {
        this.view = view
    }

    fun detachView() {
        view = null
        compositeDisposable.clear()
    }

    fun generateReport(patientId: String, type: ReportType, dateRange: DateRange) {
        view?.showLoading()
        val disposable = reportRepository.generateReport(patientId, type, dateRange)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.onReportGenerated(it)
            }, {
                view?.hideLoading()
                view?.displayError(it.message ?: "Failed to generate report.")
            })
        compositeDisposable.add(disposable)
    }

    fun loadReports(patientId: String) {
        view?.showLoading()
        val disposable = reportRepository.loadReports(patientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.displayReports(it)
            }, {
                view?.hideLoading()
                view?.displayError(it.message ?: "Failed to load reports.")
            })
        compositeDisposable.add(disposable)
    }

    fun deleteReport(reportId: String) {
        view?.showLoading()
        val disposable = reportRepository.deleteReport(reportId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.onReportDeleted()
            }, {
                view?.hideLoading()
                view?.displayError(it.message ?: "Failed to delete report.")
            })
        compositeDisposable.add(disposable)
    }

    fun shareReport(reportId: String) {
        view?.showLoading()
        val disposable = reportRepository.shareReport(reportId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.onReportShared()
            }, {
                view?.hideLoading()
                view?.displayError(it.message ?: "Failed to share report.")
            })
        compositeDisposable.add(disposable)
    }
}
