package com.fueians.medicationapp.model.services

import android.app.AlarmManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.fueians.medicationapp.model.entities.MedicationEntity
import com.fueians.medicationapp.model.entities.MedicationScheduleEntity

class ReminderService(private val context: Context) {

    private val notificationService = NotificationService(context)
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val handler = Handler(Looper.getMainLooper())

    // ---------------------------------------------------------
    //  Schedule Medication Reminder
    // ---------------------------------------------------------
    fun scheduleReminder(schedule: MedicationScheduleEntity, medication: MedicationEntity) {
        val delay = schedule.getReminderTime() - System.currentTimeMillis()
        if (delay <= 0) return // skip past reminders

        handler.postDelayed({
            notificationService.showMedicationReminder(schedule, medication)
        }, delay)
    }

    // ---------------------------------------------------------
    //  Cancel Reminder by Schedule
    // ---------------------------------------------------------
    fun cancelReminder(schedule: MedicationScheduleEntity) {
        // With Handler we cannot cancel exact delayed tasks without keeping references,
        // so this would require tracking Runnable references if needed
    }

    // ---------------------------------------------------------
    //  Reschedule Reminder
    // ---------------------------------------------------------
    fun rescheduleReminder(schedule: MedicationScheduleEntity, medication: MedicationEntity) {
        cancelReminder(schedule)
        scheduleReminder(schedule, medication)
    }

    // ---------------------------------------------------------
    //  Schedule Refill Reminder
    // ---------------------------------------------------------
//    fun scheduleRefillReminder(medication: MedicationEntity, daysBeforeRefill: Int) {
//        val reminderTime = medication.refillDateInMillis - TimeUnit.DAYS.toMillis(daysBeforeRefill.toLong())
//        val delay = reminderTime - System.currentTimeMillis()
//        if (delay <= 0) return
//
//        handler.postDelayed({
//            notificationService.showRefillReminder(medication)
//        }, delay)
//    }

    // ---------------------------------------------------------
    //  Cancel All Reminders
    // ---------------------------------------------------------
    fun cancelAllReminders() {
        handler.removeCallbacksAndMessages(null)
    }
}
