package com.fueians.medicationapp

import android.app.Application
import android.util.Log

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application started")
    }

    companion object {
        private const val TAG = "MedicationApp"
    }
}
