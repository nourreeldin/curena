package com.fueians.medicationapp.model.repository


import io.reactivex.Completable
import com.fueians.medicationapp.model.remote.SupabaseClient
import com.fueians.medicationapp.model.services.SyncService


// SyncRepository: Handles synchronization between local data and remote server.
class SyncRepository(
    private val supabaseClient: SupabaseClient,
    private val syncService: SyncService
) {
    private var lastSyncTime: Long = 0L
    private var syncInProgress: Boolean = false

    // Sync all data types
    fun syncAll(): Completable {
        return syncService.syncAll()
            .andThen(updateSyncTime(System.currentTimeMillis()))
    }


    // Sync medication data
    fun syncMedications(): Completable {
        return syncService.syncMedications()
    }


    // Sync schedule data
    fun syncSchedules(): Completable {
        return syncService.syncSchedules()
    }


    // Sync reports
    fun syncReports(): Completable {
        return syncService.syncReports()
    }


    // Get timestamp of last sync
    fun getLastSyncTime(): Long {
        return lastSyncTime
    }


    // Update sync timestamp
    fun updateSyncTime(timestamp: Long): Completable {
        return Completable.fromAction {
            lastSyncTime = timestamp
        }
    }


    // Resolve sync conflicts
    fun resolveConflicts(conflicts: List<Conflict>): Completable {
        return syncService.resolveConflicts(conflicts)
    }
}