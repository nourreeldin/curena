package com.fueians.medicationapp.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fueians.medicationapp.model.entities.RefillEntity // Assuming this entity exists
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface RefillDao {

    // 1. Live stream of refill data for a specific medication (Flowable)
    @Query("SELECT * FROM refills WHERE medication_id = :medicationId")
    fun getRefillByMedication(medicationId: String): Flowable<RefillEntity> // Assuming one active refill per medication

    // 2. Live stream of medications running low (Flowable)
    @Query("SELECT * FROM refills WHERE remaining_quantity <= :threshold")
    fun getLowStockRefills(threshold: Int): Flowable<List<RefillEntity>>

    // 3. Insert new refill record (Completable)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRefill(refill: RefillEntity): Completable

    // 4. Update refill record (Completable)
    @Update
    fun updateRefill(refill: RefillEntity): Completable
}