package com.fueians.medicationapp.model.repository

import com.fueians.medicationapp.model.dao.DrugInfoDao
import com.fueians.medicationapp.model.entities.DrugInfo

/**
 * DrugInfoRepository
 *
 * Responsibility: Provide a clean, synchronous API for drug information data operations.
 * All methods in this repository perform blocking I/O and MUST be called from a background thread.
 */
class DrugInfoRepository {

    // DAO is now a private attribute with a placeholder implementation.
    private val drugInfoDao: DrugInfoDao = object : DrugInfoDao {
        private val inMemoryDrugs = mutableMapOf<String, DrugInfo>()

        override fun searchDrugs(query: String): List<DrugInfo> {
            return inMemoryDrugs.values.filter { it.name.contains(query, ignoreCase = true) }
        }

        override fun getDrugInfoById(drugId: String): DrugInfo? {
            return inMemoryDrugs[drugId]
        }

        override fun saveDrugInfo(drugInfo: DrugInfo) {
            inMemoryDrugs[drugInfo.id] = drugInfo
        }
    }

    fun searchDrugs(query: String): List<DrugInfo> {
        return drugInfoDao.searchDrugs(query)
    }

    fun loadDrugInfo(drugId: String): DrugInfo? {
        return drugInfoDao.getDrugInfoById(drugId)
    }

    fun saveDrugInfo(drugInfo: DrugInfo) {
        drugInfoDao.saveDrugInfo(drugInfo)
    }
}
