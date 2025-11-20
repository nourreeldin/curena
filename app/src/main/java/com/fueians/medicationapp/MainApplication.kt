package com.fueians.medicationapp
import android.app.Application
import android.util.Log
import com.fueians.medicationapp.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

/**
 * MainApplication serves as the base Application class for the application.
 *
 * Notes for developers:
 * - Used to initialize global dependencies (e.g., Dagger Hilt, Supabase, database).
 * - Application-wide configuration should be done here before any Activity starts.
 * - Avoid performing heavy operations in onCreate() to prevent startup lag.
 * - Use this class to track app-level lifecycle events or analytics.
 */
// MedicationApplication.kt
class MedicationApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            // ... the setup you referenced
            androidContext(this@MedicationApplication) // 'this' refers to MedicationApplication
            modules(appModules)
        }
    }
}