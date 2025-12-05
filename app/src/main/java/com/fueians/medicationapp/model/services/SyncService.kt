package com.fueians.medicationapp.model.services

import com.fueians.medicationapp.model.clients.SupabaseClient
import com.fueians.medicationapp.model.entities.*
import com.fueians.medicationapp.model.dao.*
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

data class SyncResult(
    val success: Boolean,
    val message: String? = null,
    val syncedCount: Int = 0
)

/**
 * SyncService
 * Handles bidirectional synchronization between local Room DB and Supabase backend.
 */
class SyncService(
    private val supabaseClient: SupabaseClient,
    private val medicationDao: MedicationDao,
    private val scheduleDao: MedicationScheduleDao,
    private val reportDao: ReportDao,
    private val adherenceLogDao: AdherenceLogDao,
    private val refillDao: RefillDao
) {

    /**
     * Get current authenticated user ID
     */
    private suspend fun getCurrentUserId(): String? {
        return try {
            supabaseClient.authClient.currentUserOrNull()?.id
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Sync all entities
     */
    suspend fun syncAll(): SyncResult = withContext(Dispatchers.IO) {
        try {
            val userId = getCurrentUserId()
                ?: return@withContext SyncResult(false, "User not authenticated")

            val med = syncMedications(userId)
            val sch = syncSchedules(userId)
            val rep = syncReports(userId)
            val adh = syncAdherenceLogs(userId)
            val ref = syncRefills(userId)

            if (med.success && sch.success && rep.success && adh.success && ref.success) {
                SyncResult(
                    success = true,
                    message = "All data synced successfully.",
                    syncedCount = med.syncedCount + sch.syncedCount + rep.syncedCount +
                            adh.syncedCount + ref.syncedCount
                )
            } else {
                val errors = listOfNotNull(
                    med.message, sch.message, rep.message, adh.message, ref.message
                ).joinToString("; ")
                SyncResult(success = false, message = "Some sync operations failed: $errors")
            }
        } catch (e: Exception) {
            SyncResult(success = false, message = e.message)
        }
    }

    /**
     * Sync Medications
     */
    suspend fun syncMedications(userId: String? = null): SyncResult = withContext(Dispatchers.IO) {
        try {
            val currentUserId = userId ?: getCurrentUserId()
            ?: return@withContext SyncResult(false, "User not authenticated")

            // Get local medications
            val local = medicationDao.getAllMedicationsSync()

            // Fetch medications from Supabase
            val remote = try {
                supabaseClient.databaseClient
                    .from("medications")
                    .select()
                    .decodeList<MedicationEntity>()
                    .filter { it.userId == currentUserId }
            } catch (e: Exception) {
                return@withContext SyncResult(false, "Failed to fetch medications: ${e.message}")
            }

            // Merge and resolve conflicts
            val merged = mergeAndResolve(local, remote)

            // Upload merged data to Supabase
            merged.forEach { med ->
                try {
                    supabaseClient.databaseClient
                        .from("medications")
                        .upsert(med)
                } catch (e: Exception) {
                    // Log error but continue with other medications
                }
            }

            // Update local database
            medicationDao.insertMedications(merged)

            SyncResult(true, "Medications synced", syncedCount = merged.size)
        } catch (e: Exception) {
            SyncResult(false, "Medication sync failed: ${e.message}")
        }
    }

    /**
     * Sync Schedules
     */
    suspend fun syncSchedules(userId: String? = null): SyncResult = withContext(Dispatchers.IO) {
        try {
            val currentUserId = userId ?: getCurrentUserId()
            ?: return@withContext SyncResult(false, "User not authenticated")

            val local = scheduleDao.getAllSchedulesSync()

            // Get all user's medication IDs to filter schedules
            val userMedicationIds = medicationDao.getMedicationsByUserSync(currentUserId)
                .map { it.id }

            val remote = try {
                supabaseClient.databaseClient
                    .from("medication_schedules")
                    .select()
                    .decodeList<MedicationScheduleEntity>()
                    .filter { it.medicationId in userMedicationIds }
            } catch (e: Exception) {
                return@withContext SyncResult(false, "Failed to fetch schedules: ${e.message}")
            }

            val merged = mergeAndResolve(local, remote)

            // Upload to Supabase
            merged.forEach { schedule ->
                try {
                    supabaseClient.databaseClient
                        .from("medication_schedules")
                        .upsert(schedule)
                } catch (e: Exception) {
                    // Log error but continue
                }
            }

            // Update local database
            scheduleDao.insertSchedules(merged)

            SyncResult(true, "Schedules synced", syncedCount = merged.size)
        } catch (e: Exception) {
            SyncResult(false, "Schedule sync failed: ${e.message}")
        }
    }

    /**
     * Sync Reports
     */
    suspend fun syncReports(userId: String? = null): SyncResult = withContext(Dispatchers.IO) {
        try {
            val currentUserId = userId ?: getCurrentUserId()
            ?: return@withContext SyncResult(false, "User not authenticated")

            val local = reportDao.getAllReportsSync()

            val remote = try {
                supabaseClient.databaseClient
                    .from("reports")
                    .select()
                    .decodeList<ReportEntity>()
                    .filter { it.userId == currentUserId }
            } catch (e: Exception) {
                return@withContext SyncResult(false, "Failed to fetch reports: ${e.message}")
            }

            val merged = mergeAndResolve(local, remote)

            // Upload to Supabase
            merged.forEach { report ->
                try {
                    supabaseClient.databaseClient
                        .from("reports")
                        .upsert(report)
                } catch (e: Exception) {
                    // Log error but continue
                }
            }

            // Update local database
            reportDao.insertReports(merged)

            SyncResult(true, "Reports synced", syncedCount = merged.size)
        } catch (e: Exception) {
            SyncResult(false, "Report sync failed: ${e.message}")
        }
    }

    /**
     * Sync Adherence Logs
     */
    suspend fun syncAdherenceLogs(userId: String? = null): SyncResult = withContext(Dispatchers.IO) {
        try {
            val currentUserId = userId ?: getCurrentUserId()
            ?: return@withContext SyncResult(false, "User not authenticated")

            val local = adherenceLogDao.getAllLogsSync()

            // Calculate date range (last 30 days)
            val endDate = System.currentTimeMillis()
            val startDate = endDate - (30L * 24 * 60 * 60 * 1000)

            val remote = try {
                supabaseClient.databaseClient
                    .from("adherence_logs")
                    .select()
                    .decodeList<AdherenceLogEntity>()
                    .filter {
                        it.userId == currentUserId &&
                                it.scheduledTime >= startDate &&
                                it.scheduledTime <= endDate
                    }
            } catch (e: Exception) {
                return@withContext SyncResult(false, "Failed to fetch adherence logs: ${e.message}")
            }

            val merged = mergeAndResolve(local, remote)

            // Upload to Supabase
            merged.forEach { log ->
                try {
                    supabaseClient.databaseClient
                        .from("adherence_logs")
                        .upsert(log)
                } catch (e: Exception) {
                    // Log error but continue
                }
            }

            // Update local database
            adherenceLogDao.insertLogs(merged)

            SyncResult(true, "Adherence logs synced", syncedCount = merged.size)
        } catch (e: Exception) {
            SyncResult(false, "Adherence log sync failed: ${e.message}")
        }
    }

    /**
     * Sync Refills
     */
    suspend fun syncRefills(userId: String? = null): SyncResult = withContext(Dispatchers.IO) {
        try {
            val currentUserId = userId ?: getCurrentUserId()
            ?: return@withContext SyncResult(false, "User not authenticated")

            val local = refillDao.getAllRefillsSync()

            // Get all user's medication IDs to filter refills
            val userMedicationIds = medicationDao.getMedicationsByUserSync(currentUserId)
                .map { it.id }

            val remote = try {
                supabaseClient.databaseClient
                    .from("refills")
                    .select()
                    .decodeList<RefillEntity>()
                    .filter { it.medicationId in userMedicationIds }
            } catch (e: Exception) {
                return@withContext SyncResult(false, "Failed to fetch refills: ${e.message}")
            }

            val merged = mergeAndResolve(local, remote)

            // Upload to Supabase
            merged.forEach { refill ->
                try {
                    supabaseClient.databaseClient
                        .from("refills")
                        .upsert(refill)
                } catch (e: Exception) {
                    // Log error but continue
                }
            }

            // Update local database
            refillDao.insertRefills(merged)

            SyncResult(true, "Refills synced", syncedCount = merged.size)
        } catch (e: Exception) {
            SyncResult(false, "Refill sync failed: ${e.message}")
        }
    }

    /**
     * Conflict resolution - Last write wins based on updatedAt/timestamp
     */
    private fun <T> resolveConflict(local: T, remote: T): T {
        val localTime = when (local) {
            is MedicationEntity -> local.updatedAt
            is MedicationScheduleEntity -> local.createdAt
            is ReportEntity -> local.createdAt
            is AdherenceLogEntity -> local.timestamp
            is RefillEntity -> local.updatedAt
            else -> 0L
        }

        val remoteTime = when (remote) {
            is MedicationEntity -> remote.updatedAt
            is MedicationScheduleEntity -> remote.createdAt
            is ReportEntity -> remote.createdAt
            is AdherenceLogEntity -> remote.timestamp
            is RefillEntity -> remote.updatedAt
            else -> 0L
        }

        return if (remoteTime > localTime) remote else local
    }

    /**
     * Merge local and remote data with conflict resolution
     */
    private fun <T> mergeAndResolve(local: List<T>, remote: List<T>): List<T> {
        val map = mutableMapOf<String, T>()

        // Add all local items
        local.forEach { item ->
            val id = when (item) {
                is MedicationEntity -> item.id
                is MedicationScheduleEntity -> item.id
                is ReportEntity -> item.id
                is AdherenceLogEntity -> item.id
                is RefillEntity -> item.id
                else -> return@forEach
            }
            map[id] = item
        }

        // Merge remote items with conflict resolution
        remote.forEach { item ->
            val id = when (item) {
                is MedicationEntity -> item.id
                is MedicationScheduleEntity -> item.id
                is ReportEntity -> item.id
                is AdherenceLogEntity -> item.id
                is RefillEntity -> item.id
                else -> return@forEach
            }

            val existing = map[id]
            map[id] = if (existing != null) resolveConflict(existing, item) else item
        }

        return map.values.toList()
    }
}