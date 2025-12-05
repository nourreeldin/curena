package com.fueians.medicationapp
import android.app.Application
import android.util.Log
import com.fueians.medicationapp.model.security.SecurityManager
/**
 * MainApplication serves as the base Application class for the application.
 *
 * Notes for developers:
 * - Used to initialize global dependencies (e.g., Dagger Hilt, Supabase, database).
 * - Application-wide configuration should be done here before any Activity starts.
 * - Avoid performing heavy operations in onCreate() to prevent startup lag.
 * - Use this class to track app-level lifecycle events or analytics.
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application started")
        SecurityManager.getInstance(this).initialize()
    }
    companion object {
        private const val TAG = "MedicationApp"
    }
}
