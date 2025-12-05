package com.fueians.medicationapp.model.clients

import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

// --- Data models ---
data class DrugInfo(
    val id: String,
    val brandName: List<String>?,
    val genericName: List<String>?,
    val purpose: List<String>?,
    val warnings: List<String>?,
    val dosage: List<String>?,
    val sideEffects: List<String>?,
    val images: List<String>?
)

data class DrugInteraction(
    val drug1Id: String,
    val drug2Id: String,
    val severity: String,
    val description: String,
    val recommendations: String?
)

// --- Retrofit API interface ---
interface OpenFdaApi {
    @GET("drug/label.json")
    fun searchDrug(@Query("search") query: String, @Query("limit") limit: Int = 5): Single<DrugResponse>

    @GET("drug/label.json")
    fun getDrugById(@Query("search") drugId: String): Single<DrugResponse>

    // Additional endpoints can be added here
}

data class DrugResponse(val results: List<DrugInfo>)

// --- Drug API Client ---
class DrugAPIClient(
    private val apiBaseUrl: String,
    private val apiKey: String
) {

    private val httpClient = OkHttpClient.Builder()
        .callTimeout(15, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(apiBaseUrl)
        .client(httpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val api = retrofit.create(OpenFdaApi::class.java)

    // Cache placeholder
    private val cache = mutableMapOf<String, DrugInfo>()

    // --- Public methods ---

    fun searchDrug(query: String): Single<List<DrugInfo>> {
        return api.searchDrug("openfda.brand_name:$query")
            .map { it.results }
            .doOnSuccess { results -> results.forEach { cache[it.id] = it } }
    }

    fun getDrugById(drugId: String): Single<DrugInfo> {
        cache[drugId]?.let { return Single.just(it) }

        return api.getDrugById("id:$drugId")
            .map { it.results.first() }
            .doOnSuccess { cache[it.id] = it }
    }

    fun getDrugByNDC(ndc: String): Single<DrugInfo> {
        cache[ndc]?.let { return Single.just(it) }

        return api.searchDrug("openfda.product_ndc:$ndc")
            .map { it.results.first() }
            .doOnSuccess { cache[it.id] = it }
    }

    fun checkInteractions(drugIds: List<String>): Single<List<DrugInteraction>> {
        // Implement logic to query interaction endpoints or local DB
        return Single.just(emptyList()) // placeholder
    }

    fun getInteractionDetails(drug1Id: String, drug2Id: String): Single<DrugInteraction> {
        // Implement API call or local lookup
        return Single.just(
            DrugInteraction(drug1Id, drug2Id, "moderate", "Example interaction", "Consult doctor")
        )
    }

    fun getSideEffects(drugId: String): Single<List<String>> {
        return getDrugById(drugId).map { it.sideEffects ?: emptyList() }
    }

    fun getWarnings(drugId: String): Single<List<String>> {
        return getDrugById(drugId).map { it.warnings ?: emptyList() }
    }

    fun getDrugImages(drugId: String): Single<List<String>> {
        return getDrugById(drugId).map { it.images ?: emptyList() }
    }

}
