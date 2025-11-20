package com.fueians.medicationapp.model.dao

import androidx.room.*
import com.fueians.medicationapp.model.entities.CaregiverPatientEntity
import kotlinx.coroutines.flow.Flow

/**
 * CaregiverPatientDao
 *
 * Responsibility: Provide database access methods for caregiver-patient relationships.
 *
 * This DAO interface defines all database operations for CaregiverPatientEntity,
 * enabling queries to find relationships from both caregiver and patient
 * perspectives. Uses Flow for reactive data updates.
 *
 * Related Classes: CaregiverPatientEntity, AppDatabase
 */
@Dao
interface CaregiverPatientDao {

    /**
     * Get all patients for a caregiver
     * Returns all relationships where the user is the caregiver
     *
     * @param caregiverId Caregiver user ID
     */
    @Query("SELECT * FROM caregiver_patient WHERE caregiver_id = :caregiverId")
    suspend fun getPatientsByCaregiver(caregiverId: String): Flow<List<CaregiverPatientEntity>>

    /**
     * Get all caregivers for a patient
     * Returns all relationships where the user is the patient
     *
     * @param patientId Patient user ID
     */
    @Query("SELECT * FROM caregiver_patient WHERE patient_id = :patientId")
    suspend fun getCaregiversByPatient(patientId: String): Flow<List<CaregiverPatientEntity>>

    /**
     * Insert a caregiver-patient relationship
     *
     * @param relationship Relationship to insert
     */
    @Insert
    suspend fun insertRelationship(relationship: CaregiverPatientEntity)

    /**
     * Delete a caregiver-patient relationship
     *
     * @param relationship Relationship to delete
     */
    @Delete
    suspend fun deleteRelationship(relationship: CaregiverPatientEntity)
}