package com.fueians.medicationapp.model.repository

import android.content.Context
import com.fueians.medicationapp.model.services.SyncService
import com.fueians.medicationapp.model.services.SyncResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncRepository(context: Context, private val syncService: SyncService) {

    private val prefs = context.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)
    private var syncInProgress: Boolean = false

    companion object {
        private const val LAST_SYNC_KEY = "last_sync_time"
    }

    /**
     * Sync all data types
     */
    suspend fun syncAll(): SyncResult = withContext(Dispatchers.IO) {
        if (syncInProgress) {
            return@withContext SyncResult(false, "Sync already in progress")
        }

        try {
            syncInProgress = true
            val result = syncService.syncAll()

            if (result.success) {
                updateSyncTime(System.currentTimeMillis())
            }

            result
        } finally {
            syncInProgress = false
        }
    }

    /**
     * Sync medications only
     */
    suspend fun syncMedications(): SyncResult = withContext(Dispatchers.IO) {
        syncService.syncMedications()
    }

    /**
     * Sync schedules only
     */
    suspend fun syncSchedules(): SyncResult = withContext(Dispatchers.IO) {
        syncService.syncSchedules()
    }

    /**
     * Sync reports only
     */
    suspend fun syncReports(): SyncResult = withContext(Dispatchers.IO) {
        syncService.syncReports()
    }

    /**
     * Sync adherence logs only
     */
    suspend fun syncAdherenceLogs(): SyncResult = withContext(Dispatchers.IO) {
        syncService.syncAdherenceLogs()
    }

    /**
     * Get timestamp of last successful sync
     */
    fun getLastSyncTime(): Long {
        return prefs.getLong(LAST_SYNC_KEY, 0L)
    }

    /**
     * Update sync timestamp
     */
    private fun updateSyncTime(timestamp: Long) {
        prefs.edit().putLong(LAST_SYNC_KEY, timestamp).apply()
    }

    /**
     * Check if sync is needed (e.g., last sync was more than 1 hour ago)
     */
    fun isSyncNeeded(thresholdMillis: Long = 3600000L): Boolean {
        val lastSync = getLastSyncTime()
        return (System.currentTimeMillis() - lastSync) > thresholdMillis
    }

    /**
     * Check if sync is in progress
     */
    fun isSyncInProgress(): Boolean = syncInProgress

    /**
     * Force sync regardless of last sync time
     */
    suspend fun forceSync(): SyncResult = syncAll()
}