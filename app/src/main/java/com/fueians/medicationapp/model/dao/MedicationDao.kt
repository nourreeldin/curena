package com.fueians.medicationapp.model.dao

import androidx.room.*
import com.fueians.medicationapp.model.entities.MedicationEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface MedicationDao {
    @Query("SELECT * FROM medications")
    fun getAllMedications(): Flowable<List<MedicationEntity>>

    @Query("SELECT * FROM medications WHERE id = :id")
    fun getMedicationById(id: String): Single<MedicationEntity>

    @Query("SELECT * FROM medications WHERE name LIKE :query")
    fun searchMedications(query: String): Flowable<List<MedicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMedication(medication: MedicationEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMedication(medicationList: List<MedicationEntity>): Completable // Now accepts a LIST

    @Update
    fun updateMedication(medication: MedicationEntity): Completable

    @Delete
    fun deleteMedication(medication: MedicationEntity): Completable
}