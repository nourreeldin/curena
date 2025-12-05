package com.fueians.medicationapp.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fueians.medicationapp.model.dao.*
import com.fueians.medicationapp.model.entities.*
import com.fueians.medicationapp.model.typeconverters.*

@Database(
    entities = [
        UserEntity::class,
        MedicationEntity::class,
        MedicationScheduleEntity::class,
        AdherenceLogEntity::class,
        DrugInfoEntity::class,
        DrugInteractionEntity::class,
        NotificationSettingsEntity::class,
        CaregiverPatientEntity::class,
        RefillEntity::class,
        ReportEntity::class
    ],
    version = 1,
    exportSchema = true
    // autoMigrations = [AutoMigration(from = 1, to = 2)]
)
@TypeConverters(value = [InstantConverter::class, StringListConverters::class, LocalDateConverter::class])
abstract class AppDatabase: RoomDatabase() {
    // This function allows room db to access the entities
    abstract fun userDao(): UserDao
    abstract fun medicationDao(): MedicationDao
    abstract fun medicationScheduleDao(): MedicationScheduleDao
    abstract fun adherenceLogDao(): AdherenceLogDao
    abstract fun drugInfoDao(): DrugInfoDao
    abstract fun notificationSettingsDao(): NotificationSettingsDao
    abstract fun drugInteractionDao(): DrugInteractionDao
    abstract fun caregiverPatientDao(): CaregiverPatientDao
    abstract fun refillDao(): RefillDao
    abstract fun reportDao(): ReportDao

    // Singleton pattern
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "CurenaDB"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}