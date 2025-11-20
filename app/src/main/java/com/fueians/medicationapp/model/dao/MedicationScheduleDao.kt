package com.fueians.medicationapp.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.fueians.medicationapp.model.entities.MedicationScheduleEntity // Assuming this entity exists
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface MedicationScheduleDao {

    // 1. Live stream of schedules for a medication (Flowable)
    @Query("SELECT * FROM medication_schedules WHERE medication_id = :medicationId")
    fun getSchedulesByMedication(medicationId: String): Flowable<List<MedicationScheduleEntity>>

    // 2. Live stream of today's schedules (Flowable)
    @Query("SELECT * FROM medication_schedules WHERE scheduled_time >= :startOfDay AND scheduled_time < :endOfDay")
    fun getTodaySchedules(startOfDay: Long, endOfDay: Long): Flowable<List<MedicationScheduleEntity>>

    // 3. Live stream of missed schedules (Flowable)
    @Query("SELECT * FROM medication_schedules WHERE is_taken = 0 AND scheduled_time < :currentTime")
    fun getMissedSchedules(currentTime: Long): Flowable<List<MedicationScheduleEntity>>

    // 4. Insert schedule (Completable)
    @Insert
    fun insertSchedule(schedule: MedicationScheduleEntity): Completable

    // 5. Update schedule (Completable)
    @Update
    fun updateSchedule(schedule: MedicationScheduleEntity): Completable

    // 6. Delete schedule (Completable)
    @Delete
    fun deleteSchedule(schedule: MedicationScheduleEntity): Completable
}