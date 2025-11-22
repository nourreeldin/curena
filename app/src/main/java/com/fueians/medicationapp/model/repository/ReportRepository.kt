package com.fueians.medicationapp.model.repository

import com.fueians.medicationapp.model.dao.AdherenceLogDao
import com.fueians.medicationapp.model.dao.ReportDao
import com.fueians.medicationapp.model.entities.AdherenceLog
import com.fueians.medicationapp.model.entities.DateRange
import com.fueians.medicationapp.model.entities.ReportEntity
import com.fueians.medicationapp.model.entities.ReportType
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Date
import java.util.UUID

/**
 * ReportRepository
 *
 * Responsibility: Provide a clean, RxJava-based API for generating and managing reports.
 */
class ReportRepository {

    // DAOs are now private attributes with placeholder implementations.
    private val reportDao: ReportDao = object : ReportDao {
        private val reports = mutableMapOf<String, ReportEntity>()
        override fun getReportsForPatient(patientId: String): Flowable<List<ReportEntity>> = Flowable.just(reports.values.filter { it.patientId == patientId })
        override fun getReportById(reportId: String): Single<ReportEntity> {
            val report = reports[reportId]
            return if (report != null) Single.just(report) else Single.error(androidx.room.EmptyResultSetException("Query returned no rows"))
        }
        override fun insertReport(report: ReportEntity): Completable = Completable.fromAction { reports[report.id] = report }
        override fun deleteReport(reportId: String): Completable = Completable.fromAction { reports.remove(reportId) }
    }

    private val adherenceLogDao: AdherenceLogDao = object : AdherenceLogDao {
        override fun getLogsForPatient(patientId: String): Flowable<List<AdherenceLog>> = Flowable.just(emptyList())
        override fun insertLog(log: AdherenceLog): Completable = Completable.complete()
        override fun getMissedDoses(): Flowable<List<AdherenceLog>> = Flowable.just(emptyList())
    }

    private val backgroundScheduler = Schedulers.io()

    fun generateReport(patientId: String, type: ReportType, dateRange: DateRange): Single<ReportEntity> {
        return Single.fromCallable {
            val reportDataJson = when (type) {
                ReportType.ADHERENCE_SUMMARY -> "{\"adherence\": \"92%\"}"
                ReportType.MEDICATION_HISTORY -> "{\"medications\": [\"Aspirin\", \"Lisinopril\"]}"
                ReportType.FULL_REPORT -> "{\"adherence\": \"92%\", \"medications\": [\"Aspirin\"]}"
            }
            ReportEntity(
                id = UUID.randomUUID().toString(),
                patientId = patientId,
                generationDate = Date(),
                startDate = dateRange.startDate,
                endDate = dateRange.endDate,
                type = type,
                dataJson = reportDataJson
            )
        }.flatMap { report ->
            reportDao.insertReport(report).toSingleDefault(report)
        }.subscribeOn(backgroundScheduler)
    }

    fun loadReports(patientId: String): Flowable<List<ReportEntity>> {
        return reportDao.getReportsForPatient(patientId).subscribeOn(backgroundScheduler)
    }

    fun deleteReport(reportId: String): Completable {
        return reportDao.deleteReport(reportId).subscribeOn(backgroundScheduler)
    }

    fun shareReport(reportId: String): Completable {
        return reportDao.getReportById(reportId)
            .flatMapCompletable { report ->
                Completable.fromAction { println("Sharing report: ${report.id} of type ${report.type}") }
            }.subscribeOn(backgroundScheduler)
    }
}
