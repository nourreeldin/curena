package com.fueians.medicationapp.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fueians.medicationapp.view.screens.NotificationSettingsScreen
import com.fueians.medicationapp.view.screens.navigation.BottomNavItem
import com.fueians.medicationapp.view.screens.navigation.ScreenScaffold
import com.fueians.medicationapp.view.theme.AppTheme


class NotificationSettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                 ScreenScaffold(
                     currentItem = BottomNavItem.Profile,
                     onNavItemClick = { item ->
                         when (item) {
                             is BottomNavItem.Home ->
                                 startActivity(Intent(this, DashboardActivity::class.java))
                             is BottomNavItem.Medications ->
                                 startActivity(Intent(this, MedicationListActivity::class.java))
                             is BottomNavItem.Schedule ->
                                 startActivity(Intent(this, MedicationsScheduleActivity::class.java))
                             is BottomNavItem.Reports ->
                                 startActivity(Intent(this, ReportsActivity::class.java))
                             is BottomNavItem.Profile -> { /* already here */ }
                         }
                     }
                 ) {
                     NotificationSettingsScreen(
                         onBackClick = { finish() }
                     )
                 }
             }
        }
    }
}
