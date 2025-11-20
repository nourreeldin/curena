package com.fueians.medicationapp.model.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fueians.medicationapp.model.dao.UserDao
import com.fueians.medicationapp.model.entities.UserEntity
import com.fueians.medicationapp.model.typeconverters.InstantConverter

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = true
    // autoMigrations = [AutoMigration(from = 1, to = 2)]
)
@TypeConverters(value = [InstantConverter::class])
abstract class AppDatabase: RoomDatabase() {
    // this function allows room db to access the entities
    abstract fun userDao(): UserDao
    // singleton pattern
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "CurenaDB"
                ).fallbackToDestructiveMigration(true).build() // true for now but on release we will use auto migration or make manual
                INSTANCE = instance
                instance
            }
        }
    }
}