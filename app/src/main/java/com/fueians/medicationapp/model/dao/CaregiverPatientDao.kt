package com.fueians.medicationapp.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.fueians.medicationapp.model.entities.CaregiverPatientEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface CaregiverPatientDao {

    // --- Core Operations (must return Completable) ---

    // 1. Bulk Insert Method (KEEP this single definition)
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Added onConflict strategy for robustness
    fun insertRelationships(relationships: List<CaregiverPatientEntity>): Completable

    // 2. Delete Query
    @Query("DELETE FROM caregiver_patient WHERE caregiver_id = :caregiverId")
    fun deleteRelationshipsByCaregiver(caregiverId: String): Completable

    // --- Custom Transactional Sync Method ---

    /**
     * Replaces all existing caregiver-patient relationships for a specific caregiver/patient
     * with the new list received from the remote source in a single transaction.
     */
    @Transaction // Guarantees atomicity (all or nothing)
    fun syncRelationships(
        caregiverId: String,
        remoteList: List<CaregiverPatientEntity>
    ): Completable {
        // Logic: Delete old data, then insert new data
        return deleteRelationshipsByCaregiver(caregiverId)
            // .andThen() chains the operations: delete must complete before insert starts
            .andThen(insertRelationships(remoteList))
    }

    // -----------------------------------------------------
    // ❌ REMOVED THE DUPLICATE BELOW THIS LINE
    // @Insert
    // fun insertRelationships(relationships: List<CaregiverPatientEntity>): Completable
    // -----------------------------------------------------
}