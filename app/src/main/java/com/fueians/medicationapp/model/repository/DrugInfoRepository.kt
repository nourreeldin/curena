package com.fueians.medicationapp.model.repository

import com.fueians.medicationapp.model.dao.DrugInfoDao
import com.fueians.medicationapp.model.entities.DrugInfoEntity
import com.fueians.medicationapp.model.entities.DrugInteractionEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * DrugInfoRepository
 *
 * Responsibility: Provide a clean, RxJava-based API for drug information operations.
 */
class DrugInfoRepository(private val drugInfoDao: DrugInfoDao) {

    private val backgroundScheduler = Schedulers.io()

    fun getDrugById(id: String): Flowable<DrugInfoEntity> {
        return drugInfoDao.getDrugById(id).subscribeOn(backgroundScheduler)
    }

    fun searchDrugs(query: String): Flowable<List<DrugInfoEntity>> {
        return drugInfoDao.searchDrugs(query).subscribeOn(backgroundScheduler)
    }

    fun insertDrugInfo(drugInfo: DrugInfoEntity): Completable {
        return drugInfoDao.insertDrugInfo(drugInfo).subscribeOn(backgroundScheduler)
    }

    fun getInteractionsByDrug(drugId: String): Flowable<List<DrugInteractionEntity>> {
        return drugInfoDao.getInteractionsByDrug(drugId).subscribeOn(backgroundScheduler)
    }
}
