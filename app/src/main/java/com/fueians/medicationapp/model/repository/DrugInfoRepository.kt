package com.fueians.medicationapp.model.repository


import io.reactivex.Completable
import io.reactivex.Observable
import com.fueians.medicationapp.model.dao.DrugInfoDao
import com.fueians.medicationapp.model.entities.DrugInfoEntity

class DrugInfoRepository
    (
    private val drugInfoDao: DrugInfoDao,
    private val drugAPIClient: DrugAPIClient)
{
    // Search drugs from external API
    fun searchDrug(query: String): Observable<List<DrugInfo>> {
        return drugAPIClient.searchDrug(query)
    }


    // Get drug details by ID (remote + fallback to local cache)
    fun getDrugById(id: String): Observable<DrugInfo> {
        return drugAPIClient.getDrugById(id)
            .doOnNext { drugInfoDao.saveDrugInfo(it) }
            .onErrorResumeNext { _: Throwable -> drugInfoDao.getDrugById(id).toObservable() }
    }


    // Check interactions between multiple drugs
    fun checkInteractions(drugIds: List<String>): Observable<List<DrugInteractionService>> {
        return drugAPIClient.checkInteractions(drugIds)
    }


    // Save drug info to local cache
    fun saveDrugInfo(drugInfo: DrugInfoEntity): Completable {
        return Completable.fromAction {
            drugInfoDao.saveDrugInfo(drugInfo)
        }
    }


    // Get detailed interaction information
    fun getInteractionDetails(interactionId: String): Observable<DrugInteractionService> {
        return drugAPIClient.getInteractionDetails(interactionId)
    }



}