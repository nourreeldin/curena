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

    // ========== INSERT ==========
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelationship(relationship: CaregiverPatientEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelationships(relationships: List<CaregiverPatientEntity>)

    // ========== UPDATE ==========
    @Update
    suspend fun updateRelationship(relationship: CaregiverPatientEntity)

    // ========== DELETE ==========
    @Delete
    suspend fun deleteRelationship(relationship: CaregiverPatientEntity)

    @Query("DELETE FROM caregiver_patient WHERE id = :id")
    suspend fun deleteRelationshipById(id: String)

    // ========== QUERY - FLOW (Reactive) ==========
    @Query("SELECT * FROM caregiver_patient WHERE caregiver_id = :caregiverId ORDER BY created_at DESC")
    fun getPatientsByCaregiver(caregiverId: String): Flow<List<CaregiverPatientEntity>>

    @Query("SELECT * FROM caregiver_patient WHERE patient_id = :patientId ORDER BY created_at DESC")
    fun getCaregiversByPatient(patientId: String): Flow<List<CaregiverPatientEntity>>

    @Query("""
        SELECT * FROM caregiver_patient 
        WHERE caregiver_id = :caregiverId 
        AND invitation_status = 'ACCEPTED' 
        ORDER BY created_at DESC
    """)
    fun getActiveRelationships(caregiverId: String): Flow<List<CaregiverPatientEntity>>

    @Query("""
        SELECT * FROM caregiver_patient 
        WHERE patient_id = :patientId 
        AND invitation_status = 'PENDING' 
        ORDER BY created_at DESC
    """)
    fun getPendingInvitationsByPatient(patientId: String): Flow<List<CaregiverPatientEntity>>

    @Query("""
        SELECT * FROM caregiver_patient 
        WHERE caregiver_id = :caregiverId 
        AND invitation_status = 'PENDING' 
        ORDER BY created_at DESC
    """)
    fun getPendingInvitationsByCaregiver(caregiverId: String): Flow<List<CaregiverPatientEntity>>

    // ========== QUERY - SYNC (Non-reactive) ==========
    @Query("SELECT * FROM caregiver_patient WHERE id = :id")
    suspend fun getRelationshipById(id: String): CaregiverPatientEntity?

    @Query("SELECT * FROM caregiver_patient WHERE caregiver_id = :caregiverId AND patient_id = :patientId")
    suspend fun getRelationship(caregiverId: String, patientId: String): CaregiverPatientEntity?

    @Query("SELECT * FROM caregiver_patient ORDER BY created_at DESC")
    suspend fun getAllRelationshipsSync(): List<CaregiverPatientEntity>

    @Query("""
        SELECT * FROM caregiver_patient 
        WHERE caregiver_id = :caregiverId 
        AND patient_id = :patientId 
        AND invitation_status = 'ACCEPTED'
    """)
    suspend fun getActiveRelationship(caregiverId: String, patientId: String): CaregiverPatientEntity?

    // ========== STATISTICS ==========
    @Query("SELECT COUNT(*) FROM caregiver_patient WHERE caregiver_id = :caregiverId AND invitation_status = 'ACCEPTED'")
    suspend fun getActivePatientCount(caregiverId: String): Int

    @Query("SELECT COUNT(*) FROM caregiver_patient WHERE patient_id = :patientId AND invitation_status = 'ACCEPTED'")
    suspend fun getActiveCaregiverCount(patientId: String): Int

    @Query("SELECT COUNT(*) FROM caregiver_patient WHERE patient_id = :patientId AND invitation_status = 'PENDING'")
    suspend fun getPendingInvitationCount(patientId: String): Int
}