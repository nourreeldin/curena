package com.fueians.medicationapp.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fueians.medicationapp.model.entities.DrugInfoEntity
import com.fueians.medicationapp.model.entities.DrugInteractionEntity // Assuming this entity exists
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface DrugInfoDao {
    // Single fetch by ID (uses Single)
    @Query("SELECT * FROM drug_info WHERE id = :id")
    fun getDrugById(id: String): Single<DrugInfoEntity>

    // Live search results (uses Flowable)
    @Query("SELECT * FROM drug_info WHERE name LIKE :query")
    fun searchDrugs(query: String): Flowable<List<DrugInfoEntity>>

    // Insert operation (uses Completable)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDrugInfo(drugInfo: DrugInfoEntity): Completable

    // Live stream of interactions for a drug (uses Flowable)
    @Query("SELECT * FROM drug_interactions WHERE drug1_id = :drugId OR drug2_id = :drugId")
    fun getInteractionsByDrug(drugId: String): Flowable<List<DrugInteractionEntity>>
}