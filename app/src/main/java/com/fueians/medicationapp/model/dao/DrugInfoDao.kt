package com.fueians.medicationapp.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fueians.medicationapp.model.entities.DrugInfo

/**
 * DrugInfoDao
 *
 * Responsibility: Provide synchronous database access for locally cached drug information.
 * These methods must be called from a background thread.
 */
@Dao
interface DrugInfoDao {
    @Query("SELECT * FROM drug_info WHERE name LIKE '%' || :query || '%' LIMIT 20")
    fun searchDrugs(query: String): List<DrugInfo>

    @Query("SELECT * FROM drug_info WHERE id = :drugId")
    fun getDrugInfoById(drugId: String): DrugInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveDrugInfo(drugInfo: DrugInfo)
}
