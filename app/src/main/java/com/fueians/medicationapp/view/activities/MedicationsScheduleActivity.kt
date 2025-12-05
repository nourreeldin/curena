package com.fueians.medicationapp.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.fueians.medicationapp.view.screens.MedicationScheduleItemUi
import com.fueians.medicationapp.view.screens.MedicationScheduleScreen
import com.fueians.medicationapp.view.screens.navigation.BottomNavItem
import com.fueians.medicationapp.view.screens.navigation.ScreenScaffold
import com.fueians.medicationapp.view.theme.AppTheme

class MedicationsScheduleActivity : ComponentActivity() {

    private val schedules: SnapshotStateList<MedicationScheduleItemUi> =
        mutableStateListOf(
            MedicationScheduleItemUi("Metformin", "8:00 AM, 8:00 PM", "Daily", true),
            MedicationScheduleItemUi("Lisinopril", "10:00 AM", "Daily", true),
            MedicationScheduleItemUi("Atorvastatin", "9:00 PM", "Daily", true),
            MedicationScheduleItemUi("Vitamin D", "8:00 AM", "Weekly (Sunday)", false)
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                ScheduleHost()
            }
        }
    }

    @Composable
    private fun ScheduleHost() {
        ScreenScaffold(
            currentItem = BottomNavItem.Schedule,
            onNavItemClick = { item ->
                when (item) {
                    is BottomNavItem.Home ->
                        startActivity(Intent(this@MedicationsScheduleActivity, DashboardActivity::class.java))

                    is BottomNavItem.Medications ->
                        startActivity(Intent(this@MedicationsScheduleActivity, MedicationListActivity::class.java))

                    is BottomNavItem.Schedule -> { /* already here */ }

                    is BottomNavItem.Reports ->
                        startActivity(Intent(this@MedicationsScheduleActivity, ReportsActivity::class.java))

                    is BottomNavItem.Profile ->
                        startActivity(Intent(this@MedicationsScheduleActivity, ProfileActivity::class.java))
                }
            }
        ) {
            MedicationScheduleScreen(
                todaySummary = listOf(
                    "8:00 AM  Metformin 500mg  – Done",
                    "10:00 AM  Lisinopril 10mg – Missed",
                    "2:00 PM  Metformin 500mg – Upcoming",
                    "9:00 PM  Atorvastatin 20mg – Upcoming"
                ),
                schedules = schedules,
                onToggleSchedule = { index, enabled ->
                    schedules[index] = schedules[index].copy(isEnabled = enabled)
                },
                onNewScheduleClick = {
                    startActivity(
                        Intent(
                            this@MedicationsScheduleActivity,
                            CreateScheduleActivity::class.java
                        )
                    )
                }
            )
        }
    }
}
