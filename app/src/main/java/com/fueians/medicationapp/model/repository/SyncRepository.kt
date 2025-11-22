package com.fueians.medicationapp.model.repository

import com.fueians.medicationapp.model.dao.MedicationDao
import com.fueians.medicationapp.model.dao.ScheduleDao
import com.fueians.medicationapp.model.dao.UserDao
import com.fueians.medicationapp.model.entities.Medication
import com.fueians.medicationapp.model.entities.MedicationSchedule
import com.fueians.medicationapp.model.entities.UserEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * A placeholder interface for a remote API client.
 */
interface RemoteApi {
    fun pullUsers(): Single<List<UserEntity>>
    fun pullMedications(): Single<List<Medication>>
    fun pushMedications(medications: List<Medication>): Completable
}

/**
 * SyncRepository
 *
 * Responsibility: Handle the synchronization of data between the local database and a remote server.
 */
class SyncRepository {

    // DAOs and services are now private attributes with placeholder implementations.
    private val userDao: UserDao = object : UserDao { /* ... placeholder ... */ }
    private val medicationDao: MedicationDao = object : MedicationDao { /* ... placeholder ... */ }
    private val scheduleDao: ScheduleDao = object : ScheduleDao { /* ... placeholder ... */ }

    private val remoteApi: RemoteApi = object : RemoteApi {
        override fun pullUsers(): Single<List<UserEntity>> = Single.just(emptyList())
        override fun pullMedications(): Single<List<Medication>> = Single.just(emptyList())
        override fun pushMedications(medications: List<Medication>): Completable = Completable.fromAction { println("Pushed ${medications.size} medications.") }
    }

    private val backgroundScheduler = Schedulers.io()

    fun pullFromServer(): Completable {
        val pullUsers = remoteApi.pullUsers().flatMapCompletable { users -> Completable.fromAction { users.forEach { userDao.updateUser(it) } } }
        val pullMedications = remoteApi.pullMedications().flatMapCompletable { medications -> Completable.fromAction { medications.forEach { medicationDao.updateMedication(it) } } }

        return Completable.mergeArray(pullUsers, pullMedications)
            .doOnComplete { println("Sync complete: Pulled data from server.") }
            .subscribeOn(backgroundScheduler)
    }

    fun pushToServer(): Completable {
        return medicationDao.getMedicationsForUser("all").firstOrError() // Simplified
            .flatMapCompletable { localMedications ->
                remoteApi.pushMedications(localMedications)
            }
            .doOnComplete { println("Push complete: Sent data to server.") }
            .subscribeOn(backgroundScheduler)
    }
}
