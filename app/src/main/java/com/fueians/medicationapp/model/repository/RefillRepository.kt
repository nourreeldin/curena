package com.fueians.medicationapp.model.repository

package com.fueians.medicationapp.model.repository

import com.fueians.medicationapp.model.dao.RefillDao
import com.fueians.medicationapp.model.entities.RefillEntity
import com.fueians.medicationapp.model.remote.SupabaseClient // ⚠️ NEW IMPORT
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers

class RefillRepository(
    private val refillDao: RefillDao,
    private val supabaseClient: SupabaseClient // ⚠️ NEW DEPENDENCY
) {
    private val ioScheduler = Schedulers.io()

    fun observeRefillByMedication(medicationId: String): Flowable<RefillEntity> {
        // Trigger network sync on subscription
        val syncCompletable = supabaseClient.fetchRefillByMedication(medicationId) // Assume this method exists
            .flatMapCompletable { remoteRefill ->
                // Logic to replace local refill status
                refillDao.insertRefill(remoteRefill) // insertRefill returns Completable
            }
            .subscribeOn(ioScheduler)

        return refillDao.getRefillByMedication(medicationId)
            .subscribeOn(ioScheduler)
            .doOnSubscribe { syncCompletable.subscribe({}, { error -> println("Refill sync failed: $error") }) }
    }

    // Existing methods
    fun saveRefill(refill: RefillEntity): Completable {
        return refillDao.insertRefill(refill)
            .andThen(supabaseClient.uploadRefill(refill)) // Assume uploadRefill returns Completable
            .subscribeOn(ioScheduler)
    }

    fun observeLowStockRefills(threshold: Int): Flowable<List<RefillEntity>> {
        return refillDao.getLowStockRefills(threshold)
            .subscribeOn(ioScheduler)
    }


    fun updateRefill(refill: RefillEntity): Completable {
        return refillDao.updateRefill(refill)
            .subscribeOn(ioScheduler)
    }
}