package com.fueians.medicationapp.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.fueians.medicationapp.model.entities.AdherenceLogEntity // Assuming this entity exists
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface AdherenceLogDao {

    // 1. Live stream of logs for a specific medication, ordered by time (Flowable)
    @Query("SELECT * FROM adherence_logs WHERE medication_id = :medicationId ORDER BY timestamp DESC")
    fun getLogsByMedication(medicationId: String): Flowable<List<AdherenceLogEntity>>

    // 2. Live stream of logs within a date range (Flowable)
    // Note: startDate and endDate are assumed to be Long (timestamps)
    @Query("SELECT * FROM adherence_logs WHERE timestamp BETWEEN :startDate AND :endDate")
    fun getLogsByDateRange(startDate: Long, endDate: Long): Flowable<List<AdherenceLogEntity>>

    // 3. Insert new log (Completable)
    @Insert
    fun insertLog(log: AdherenceLogEntity): Completable

    // 4. Update existing log (Completable)
    @Update
    fun updateLog(log: AdherenceLogEntity): Completable
}