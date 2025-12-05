package com.fueians.medicationapp.model.repository

import android.content.Context
import com.fueians.medicationapp.model.dao.DrugInfoDao
import com.fueians.medicationapp.model.clients.DrugAPIClient
import com.fueians.medicationapp.model.clients.DrugInfo
import com.fueians.medicationapp.model.clients.DrugInteraction
import com.fueians.medicationapp.model.entities.DrugInfoEntity
import com.fueians.medicationapp.model.entities.DrugInteractionEntity
import com.fueians.medicationapp.model.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.reactivex.rxjava3.core.Single

class DrugInfoRepository(
    context: Context,
    private val drugAPIClient: DrugAPIClient
) {

    private val drugInfoDao: DrugInfoDao by lazy {
        AppDatabase.getInstance(context).drugInfoDao()
    }

    /**
     * Search drugs from external API with local caching
     */
    fun searchDrug(query: String): Single<List<DrugInfo>> {
        return drugAPIClient.searchDrug(query)
            .doOnSuccess { drugs ->
                // Cache results locally
                drugs.forEach { drug ->
                    val entity = mapToEntity(drug)
                    drugInfoDao.insertDrugInfo(entity)
                }
            }
            .onErrorResumeNext { error ->
                // Fallback to local cache
                Single.fromCallable {
                    drugInfoDao.searchDrugs(query)
                        .map { mapFromEntity(it) }
                }
            }
    }

    /**
     * Get drug details by ID
     */
    fun getDrugById(id: String): Single<DrugInfo> {
        // Check cache first
        val cached = drugInfoDao.getDrugById(id)
        if (cached != null && cached.isCacheValid()) {
            return Single.just(mapFromEntity(cached))
        }

        // Fetch from API
        return drugAPIClient.getDrugById(id)
            .doOnSuccess { drug ->
                val entity = mapToEntity(drug)
                drugInfoDao.insertDrugInfo(entity)
            }
            .onErrorResumeNext { error ->
                // Fallback to cache even if expired
                if (cached != null) {
                    Single.just(mapFromEntity(cached))
                } else {
                    Single.error(error)
                }
            }
    }

    /**
     * Get drug interaction
     */
    suspend fun getInteraction(drug1: String, drug2: String): DrugInteractionEntity? =
        withContext(Dispatchers.IO) {
            drugInfoDao.getInteraction(drug1, drug2)
        }

    /**
     * Check interactions between multiple drugs
     */
    fun checkInteractions(drugIds: List<String>): Single<List<DrugInteraction>> {
        return drugAPIClient.checkInteractions(drugIds)
            .doOnSuccess { interactions ->
                // Cache interactions locally
                interactions.forEach { interaction ->
                    val entity = mapInteractionToEntity(interaction)
                    drugInfoDao.insertInteraction(entity)
                }
            }
    }

    /**
     * Save drug info to local cache
     */
    suspend fun saveDrugInfo(drugInfo: DrugInfoEntity) = withContext(Dispatchers.IO) {
        drugInfoDao.insertDrugInfo(drugInfo)
    }

    /**
     * Get side effects for drug
     */
    fun getSideEffects(drugId: String): Single<List<String>> {
        return drugAPIClient.getSideEffects(drugId)
    }

    /**
     * Get warnings for drug
     */
    fun getWarnings(drugId: String): Single<List<String>> {
        return drugAPIClient.getWarnings(drugId)
    }

    // Mapping functions
    private fun mapToEntity(drug: DrugInfo): DrugInfoEntity {
        return DrugInfoEntity(
            id = drug.id,
            name = drug.brandName?.firstOrNull() ?: "",
            genericName = drug.genericName?.firstOrNull(),
            brandNames = drug.brandName ?: emptyList(),
            description = drug.purpose?.joinToString(", ") ?: "",
            sideEffects = drug.sideEffects ?: emptyList(),
            warnings = drug.warnings ?: emptyList()
        )
    }

    private fun mapFromEntity(entity: DrugInfoEntity): DrugInfo {
        return DrugInfo(
            id = entity.id,
            brandName = entity.brandNames,
            genericName = listOfNotNull(entity.genericName),
            purpose = listOf(entity.description),
            warnings = entity.warnings,
            dosage = emptyList(),
            sideEffects = entity.sideEffects,
            images = emptyList()
        )
    }

    fun mapInteractionToEntity(interaction: DrugInteraction): DrugInteractionEntity {
        return DrugInteractionEntity(
            id = "${interaction.drug1Id}_${interaction.drug2Id}",
            drug1Id = interaction.drug1Id,
            drug2Id = interaction.drug2Id,
            drug1Name = interaction.drug1Id,
            drug2Name = interaction.drug2Id,
            severity = interaction.severity,
            description = interaction.description,
            recommendations = interaction.recommendations ?: ""
        )
    }
}
