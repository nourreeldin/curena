package com.fueians.medicationapp.model.dao

import androidx.room.*
import com.fueians.medicationapp.model.entities.NotificationSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationSettingsDao {

    // ========== INSERT ==========
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: NotificationSettingsEntity)

    // ========== UPDATE ==========
    @Update
    suspend fun updateSettings(settings: NotificationSettingsEntity)

    // ========== DELETE ==========
    @Delete
    suspend fun deleteSettings(settings: NotificationSettingsEntity)

    @Query("DELETE FROM notification_settings WHERE id = :id")
    suspend fun deleteSettingsById(id: String)

    // ========== QUERY - FLOW (Reactive) ==========
    @Query("SELECT * FROM notification_settings WHERE user_id = :userId")
    fun getSettingsByUserFlow(userId: String): Flow<NotificationSettingsEntity?>

    // ========== QUERY - SYNC (Non-reactive) ==========
    @Query("SELECT * FROM notification_settings WHERE id = :id")
    suspend fun getSettingsById(id: String): NotificationSettingsEntity?

    @Query("SELECT * FROM notification_settings WHERE user_id = :userId")
    suspend fun getSettingsByUser(userId: String): NotificationSettingsEntity?

    @Query("SELECT * FROM notification_settings")
    suspend fun getAllSettings(): List<NotificationSettingsEntity>

    // ========== UTILITY ==========
    @Query("SELECT EXISTS(SELECT 1 FROM notification_settings WHERE user_id = :userId)")
    suspend fun hasSettings(userId: String): Boolean

    @Query("""
        SELECT * FROM notification_settings 
        WHERE user_id = :userId 
        AND medication_reminders_enabled = 1
    """)
    suspend fun getUsersWithMedicationReminders(userId: String): NotificationSettingsEntity?
}
