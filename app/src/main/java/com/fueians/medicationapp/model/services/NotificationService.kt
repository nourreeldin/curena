package com.fueians.medicationapp.model.services
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.fueians.medicationapp.model.entities.MedicationEntity
import com.fueians.medicationapp.model.entities.MedicationScheduleEntity
import com.fueians.medicationapp.model.entities.RefillEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationService(private val context: Context) {

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val CHANNEL_ID = "medication_notifications"

    init { createNotificationChannel() }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Medication Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Channel for medication reminder notifications"
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showMedicationReminder(schedule: MedicationScheduleEntity, medication: MedicationEntity) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Time for your medication")
            .setContentText("Take: ${medication.name} - ${schedule.dosageAmount} at ${formatTime(schedule.scheduledTime)}")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(schedule.id.hashCode(), notification)
    }

    fun sendRefillReminder(refill: RefillEntity) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Refill Needed Soon")
            .setContentText("Your medication \"${refill.id}\" has only ${refill.remainingQuantity} pills left. Time to refill!")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Unique notification ID based on refill ID
        notificationManager.notify(refill.id.hashCode() + 3000, notification)
    }


    fun showMissedDoseAlert(schedule: MedicationScheduleEntity, medication: MedicationEntity) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Missed Dose Alert")
            .setContentText("You missed your dose of ${medication.name} (${schedule.dosageAmount})")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(schedule.id.hashCode() + 1000, notification)
    }

    fun showRefillReminder(medication: MedicationEntity) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Refill Reminder")
            .setContentText("You need to refill: ${medication.name}")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(medication.id.hashCode() + 2000, notification)
    }

    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    fun updateNotificationSettings(notificationsEnabled: Boolean) {
        if (!notificationsEnabled) {
            notificationManager.cancelAll()
        }
    }

    private fun formatTime(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return format.format(date)
    }
}
