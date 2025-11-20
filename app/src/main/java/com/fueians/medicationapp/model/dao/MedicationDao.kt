package com.fueians.medicationapp.model.dao

import androidx.room.*
import com.fueians.medicationapp.model.entities.MedicationEntity
import kotlinx.coroutines.flow.Flow

/**
 * MedicationDao
 *
 * Responsibility: Provide database access methods for medication data.
 *
 * This DAO interface defines all database operations for MedicationEntity,
 * including CRUD operations, queries, and search functionality. Uses Flow
 * for reactive data updates.
 *
 * Related Classes: MedicationEntity, AppDatabase
 */
@Dao
interface MedicationDao {

    /**
     * Get all medications
     * Returns a Flow that emits the list whenever data changes
     */
    @Query("SELECT * FROM medications")
    suspend fun getAllMedications(): Flow<List<MedicationEntity>>

    /**
     * Get medication by ID
     * Returns a Flow that emits the medication or null
     *
     * @param id Medication ID
     */
    @Query("SELECT * FROM medications WHERE id = :id")
    suspend fun getMedicationById(id: String): Flow<MedicationEntity?>

    /**
     * Insert a medication
     * Replaces if medication with same ID already exists
     *
     * @param medication Medication to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: MedicationEntity)

    /**
     * Update a medication
     *
     * @param medication Medication to update
     */
    @Update
    suspend fun updateMedication(medication: MedicationEntity)

    /**
     * Delete a medication
     *
     * @param medication Medication to delete
     */
    @Delete
    suspend fun deleteMedication(medication: MedicationEntity)

    /**
     * Search medications by name
     * Uses LIKE query for partial matching
     *
     * @param query Search query (use '%query%' for contains search)
     */
    @Query("SELECT * FROM medications WHERE name LIKE :query")
    suspend fun searchMedications(query: String): Flow<List<MedicationEntity>>
}