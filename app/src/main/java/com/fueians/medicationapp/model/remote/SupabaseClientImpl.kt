package com.fueians.medicationapp.model.remote

import com.fueians.medicationapp.model.entities.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.CompletableSource

// This class would contain the actual API calls (e.g., using Retrofit, Ktor, or the Supabase SDK)
class SupabaseClientImpl : SupabaseClient {

    // All methods must return an observable that completes immediately (for successful compilation)

    override fun signIn(email: String, password: String): Single<UserEntity> =
        Single.error(NotImplementedError("Supabase sign-in not implemented"))

    override fun signUp(email: String, password: String, name: String): Single<UserEntity> =
        Single.error(NotImplementedError("Supabase sign-up not implemented"))

    override fun signOut(): Completable = Completable.complete()

    // Example of a data fetch placeholder:
    override fun fetchMedications(userId: String): Single<List<MedicationEntity>> =
        Single.just(emptyList())

    // All other methods must return a valid, stubbed RxJava type (e.g., Completable.complete())
    override fun uploadMedication(medication: MedicationEntity): Single<MedicationEntity> = Single.just(medication)
    override fun deleteMedication(medicationId: String): Completable = Completable.complete()
    override fun fetchCaregiverPatients(caregiverId: String): Single<List<CaregiverPatientEntity>> = Single.just(emptyList())
    override fun uploadRelationship(relationship: CaregiverPatientEntity): CompletableSource = Completable.complete()
    override fun deleteRelationship(id: String): CompletableSource = Completable.complete()
    override fun fetchSchedulesByMedication(medicationId: String): Single<List<MedicationScheduleEntity>> = Single.just(emptyList())
    override fun uploadSchedule(schedule: MedicationScheduleEntity): CompletableSource = Completable.complete()
    override fun deleteSchedule(id: String): CompletableSource = Completable.complete()
    override fun uploadAdherenceLog(log: AdherenceLogEntity): CompletableSource = Completable.complete()
    override fun fetchAdherenceLogs(medicationId: String): Single<List<AdherenceLogEntity>> = Single.just(emptyList())
    override fun fetchRefillByMedication(medicationId: String): Single<RefillEntity> = Single.error(NotImplementedError())
    override fun uploadRefill(refill: RefillEntity): Completable = Completable.complete()
    override fun deleteRefill(id: String): Completable = Completable.complete()
    override fun fetchReports(userId: String): Single<List<ReportEntity>> = Single.just(emptyList())
    override fun uploadReport(report: ReportEntity): Completable = Completable.complete()
    override fun deleteReport(id: String): Completable = Completable.complete()
}