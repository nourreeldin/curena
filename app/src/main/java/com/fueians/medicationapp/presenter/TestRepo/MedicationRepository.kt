package com.fueians.medicationapp.presenter.TestRepo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

// =========================================================================
// 1. Placeholder Data & DAO Interfaces
// These would be in your /model directory in a real app.
// =========================================================================

data class Medication(
    val id: String,
    val name: String,
    val dosage: String
)

data class Refill(
    val medicationId: String,
    val nextRefillDate: Long
)

@Dao
interface MedicationDao {
    @Query("SELECT * FROM Medication")
    fun getAllMedications(): Flow<List<Medication>>

    @Query("SELECT * FROM Medication WHERE id = :medicationId")
    fun getMedicationById(medicationId: String): Flow<Medication?>

    @Query("SELECT * FROM Medication WHERE name LIKE '%' || :query || '%'")
    fun searchMedications(query: String): Flow<List<Medication>>

    @Insert
    suspend fun insertMedication(medication: Medication)

    @Update
    suspend fun updateMedication(medication: Medication)

    @Delete
    suspend fun deleteMedication(medication: Medication)
}

@Dao
interface RefillDao {
    @Update
    suspend fun updateRefill(refill: Refill)
}

// =========================================================================
// 2. Medication Repository
// =========================================================================

/**
 * MedicationRepository
 *
 * Responsibility: Provide a clean API for medication and refill data operations.
 * This is the single source of truth for medication data from the local database.
 *
 * Related Classes: MedicationDao, RefillDao, MedicationPresenter
 */
class MedicationRepository(
    private val medicationDao: MedicationDao,
    private val refillDao: RefillDao,
    private val backgroundDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun loadMedications(): Flow<List<Medication>> {
        return medicationDao.getAllMedications()
    }

    fun loadMedicationDetails(medicationId: String): Flow<Medication?> {
        return medicationDao.getMedicationById(medicationId)
    }

    suspend fun addMedication(medication: Medication) = withContext(backgroundDispatcher) {
        medicationDao.insertMedication(medication)
    }

    suspend fun updateMedication(medication: Medication) = withContext(backgroundDispatcher) {
        medicationDao.updateMedication(medication)
    }

    suspend fun deleteMedication(medication: Medication) = withContext(backgroundDispatcher) {
        medicationDao.deleteMedication(medication)
    }

    fun searchMedications(query: String): Flow<List<Medication>> {
        return medicationDao.searchMedications(query)
    }

    suspend fun updateRefillStatus(refill: Refill) = withContext(backgroundDispatcher) {
        refillDao.updateRefill(refill)
    }
}
