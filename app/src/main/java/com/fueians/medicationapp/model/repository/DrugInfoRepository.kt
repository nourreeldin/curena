package com.fueians.medicationapp.model.repository

import com.fueians.medicationapp.model.dao.DrugInfoDao
import com.fueians.medicationapp.model.entities.DrugInfoEntity
import com.fueians.medicationapp.model.entities.DrugInteractionEntity
import com.fueians.medicationapp.model.remote.DrugAPIClient
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class DrugInfoRepository(private val drugInfoDao: DrugInfoDao,
                         private val drugApiClient: DrugAPIClient) {
    private val ioScheduler = Schedulers.io()

    fun fetchDrugById(id: String): Single<DrugInfoEntity> {
        // Example: Try cache first, fall back to network if error/not found
        return drugInfoDao.getDrugById(id)
            .subscribeOn(ioScheduler)
            .onErrorResumeNext { error ->
                // If local fetch fails, try network
                drugApiClient.getDrugById(id)
                    .flatMap { remoteDrug ->
                        // Save new data to cache and return the result
                        drugInfoDao.insertDrugInfo(remoteDrug)
                            .andThen(Single.just(remoteDrug))
                    }
                    .subscribeOn(ioScheduler) // Ensure network work is on I/O thread
            }
    }

    fun observeSearchDrugs(query: String): Flowable<List<DrugInfoEntity>> {
        // For searching, you might observe the local cache, but trigger a network sync first.
        val networkSearch = drugApiClient.searchDrug(query)
            .flatMapCompletable { remoteList ->
                // Assuming you have a bulk insert method for DrugInfoDao
                Completable.fromAction { /* drugInfoDao.insertDrugInfo(remoteList) */ }
            }
            .subscribeOn(ioScheduler)

        return drugInfoDao.searchDrugs(query)
            .subscribeOn(ioScheduler)
            .doOnSubscribe { networkSearch.subscribe() } // Trigger network search on subscription
    }

    // Existing methods remain the same, just wrapped with subscribeOn(ioScheduler)
    fun saveDrugInfo(drugInfo: DrugInfoEntity): Completable {
        // When saving locally, you might also update the remote source here.
        return drugInfoDao.insertDrugInfo(drugInfo)
            .subscribeOn(ioScheduler)
    }

    fun observeInteractionsByDrug(drugId: String): Flowable<List<DrugInteractionEntity>> {
        // Interactions are highly volatile; often fetched directly from the network.
        // We'll keep it simple by observing the local cache (DrugInfoDao) for now.
        return drugInfoDao.getInteractionsByDrug(drugId)
            .subscribeOn(ioScheduler)
    }
}