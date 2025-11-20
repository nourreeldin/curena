package com.fueians.medicationapp.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fueians.medicationapp.model.dao.*
import com.fueians.medicationapp.model.entities.*
import com.fueians.medicationapp.model.typeconverters.InstantConverter
import com.fueians.medicationapp.model.typeconverters.ListStringConverter


/**
 * The Room Database abstract class.
 * It links all Entities, DAOs, and Type Converters together.
 */

@Database(
    entities = [
        // Core User and Medication
        UserEntity::class,
        MedicationEntity::class,

        // Scheduling and Adherence
        MedicationScheduleEntity::class,
        AdherenceLogEntity::class,

        // Drug Information
        DrugInfoEntity::class,
        DrugInteractionEntity::class,

        // Auxiliary Tracking & Relationships
        CaregiverPatientEntity::class,
        RefillEntity::class,
        ReportEntity::class
    ],
    version = 1,
    exportSchema = false
)
// Register the converter for java.time.Instant used in UserEntity
@TypeConverters(InstantConverter::class, ListStringConverter::class)
abstract class AppDatabase : RoomDatabase() {

    // --- Access methods for all 8 DAOs ---

    abstract fun userDao(): UserDao
    abstract fun medicationDao(): MedicationDao
    abstract fun drugInfoDao(): DrugInfoDao
    abstract fun caregiverPatientDao(): CaregiverPatientDao
    abstract fun adherenceLogDao(): AdherenceLogDao
    abstract fun refillDao(): RefillDao
    abstract fun reportDao(): ReportDao
    abstract fun medicationScheduleDao(): MedicationScheduleDao
}