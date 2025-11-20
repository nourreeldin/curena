package com.fueians.medicationapp.model.remote

import com.fueians.medicationapp.model.entities.* // Import all entities
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.CompletableSource

interface SupabaseClient {
    // --- Authentication (Completed) ---
    fun signIn(email: String, password: String): Single<UserEntity>
    fun signUp(email: String, password: String, name: String): Single<UserEntity>
    fun signOut(): Completable

    // --- Medication (Completed) ---
    fun fetchMedications(userId: String): Single<List<MedicationEntity>>
    fun uploadMedication(medication: MedicationEntity): Single<MedicationEntity>
    fun deleteMedication(medicationId: String): Completable

    // --- Caregiver (Completed) ---
    fun fetchCaregiverPatients(caregiverId: String): Single<List<CaregiverPatientEntity>>
    fun uploadRelationship(relationship: CaregiverPatientEntity): CompletableSource
    fun deleteRelationship(id: String): CompletableSource

    // --- Medication Schedule (Completed) ---
    fun fetchSchedulesByMedication(medicationId: String): Single<List<MedicationScheduleEntity>>
    fun uploadSchedule(schedule: MedicationScheduleEntity): CompletableSource
    fun deleteSchedule(id: String): CompletableSource

    // --- Adherence Log (FIXED 🎯) ---
    fun uploadAdherenceLog(log: AdherenceLogEntity): CompletableSource
    // 🎯 FIX: Added the required return type (Single<List<...>>)
    fun fetchAdherenceLogs(medicationId: String): Single<List<AdherenceLogEntity>>

    // --- Refill (NEW ADDITIONS 🎯) ---
    fun fetchRefillByMedication(medicationId: String): Single<RefillEntity>
    fun uploadRefill(refill: RefillEntity): Completable
    fun deleteRefill(id: String): Completable

    // --- Report (NEW ADDITIONS 🎯) ---
    fun fetchReports(userId: String): Single<List<ReportEntity>>
    fun uploadReport(report: ReportEntity): Completable
    fun deleteReport(id: String): Completable

    // --- Drug API Integration (If the DrugInfoRepository uses Supabase for fetching info) ---
    // If you have a separate DrugAPIClient interface, these would be there.
    // Example methods often defined here:
    // fun fetchDrugInfo(query: String): Single<List<DrugInfoEntity>>
}