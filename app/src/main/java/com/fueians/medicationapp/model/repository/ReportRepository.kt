package com.fueians.medicationapp.model.repository

import com.fueians.medicationapp.model.dao.ReportDao
import com.fueians.medicationapp.model.entities.ReportEntity
import com.fueians.medicationapp.model.remote.SupabaseClient // ⚠️ NEW IMPORT
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class ReportRepository(
    private val reportDao: ReportDao,
    private val supabaseClient: SupabaseClient // ⚠️ NEW DEPENDENCY
) {
    private val ioScheduler = Schedulers.io()

    fun observeAllReports(userId: String): Flowable<List<ReportEntity>> {
        // Trigger network sync on subscription
        val syncCompletable = supabaseClient.fetchReports(userId) // Assume this method exists
            .flatMapCompletable { remoteList ->
                // Logic to sync reports
                Completable.fromAction { /* reportDao.insertReports(remoteList) */ }
            }
            .subscribeOn(ioScheduler)

        return reportDao.getAllReports()
            .subscribeOn(ioScheduler)
            .doOnSubscribe { syncCompletable.subscribe({}, { error -> println("Report sync failed: $error") }) }
    }

    // Existing methods
    fun saveReport(report: ReportEntity): Completable {
        return reportDao.insertReport(report)
            .andThen(supabaseClient.uploadReport(report)) // Assume uploadReport returns Completable
            .subscribeOn(ioScheduler)
    }

    fun deleteReport(report: ReportEntity): Completable {
        return reportDao.deleteReport(report)
            .andThen(supabaseClient.deleteReport(report.id)) // Assume deleteReport returns Completable
            .subscribeOn(ioScheduler)
    }

    fun observeAllReports(): Flowable<List<ReportEntity>> {
        return reportDao.getAllReports()
            .subscribeOn(ioScheduler)
    }

    fun fetchReportById(id: String): Single<ReportEntity> {
        return reportDao.getReportById(id)
            .subscribeOn(ioScheduler)
    }
}