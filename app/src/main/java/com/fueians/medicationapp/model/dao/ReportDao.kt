package com.fueians.medicationapp.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.fueians.medicationapp.model.entities.ReportEntity // Assuming this entity exists
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface ReportDao {

    // 1. Live stream of all reports, ordered by creation time (Flowable)
    @Query("SELECT * FROM reports ORDER BY created_at DESC")
    fun getAllReports(): Flowable<List<ReportEntity>>

    // 2. Single fetch by ID (Single)
    @Query("SELECT * FROM reports WHERE id = :id")
    fun getReportById(id: String): Single<ReportEntity>

    // 3. Insert report (Completable)
    @Insert
    fun insertReport(report: ReportEntity): Completable

    // 4. Delete report (Completable)
    @Delete
    fun deleteReport(report: ReportEntity): Completable
}