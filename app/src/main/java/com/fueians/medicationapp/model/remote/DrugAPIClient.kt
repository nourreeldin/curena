package com.fueians.medicationapp.model.remote

import com.fueians.medicationapp.model.entities.DrugInfoEntity
import com.fueians.medicationapp.model.entities.DrugInteractionEntity
import io.reactivex.rxjava3.core.Single

interface DrugAPIClient {
    fun searchDrug(query: String): Single<List<DrugInfoEntity>>
    fun getDrugById(drugId: String): Single<DrugInfoEntity>
    fun checkInteractions(drugIds: List<String>): Single<List<DrugInteractionEntity>>
}