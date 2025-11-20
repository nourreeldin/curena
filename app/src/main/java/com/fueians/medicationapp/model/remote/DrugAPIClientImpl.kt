package com.fueians.medicationapp.model.remote

import com.fueians.medicationapp.model.entities.DrugInfoEntity
import com.fueians.medicationapp.model.entities.DrugInteractionEntity
import io.reactivex.rxjava3.core.Single

/**
 * Placeholder concrete implementation for the external drug API.
 * This needs to be replaced with actual Retrofit/network calls.
 */
class DrugAPIClientImpl : DrugAPIClient {

    // Return an observable that emits an empty list immediately
    override fun searchDrug(query: String): Single<List<DrugInfoEntity>> =
        Single.just(emptyList())

    // Return an observable that emits an error or a placeholder entity
    override fun getDrugById(drugId: String): Single<DrugInfoEntity> =
        Single.error(NotImplementedError("Drug API call not yet implemented."))

    // Return an observable that emits an empty list immediately
    override fun checkInteractions(drugIds: List<String>): Single<List<DrugInteractionEntity>> =
        Single.just(emptyList())
}