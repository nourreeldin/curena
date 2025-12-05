package com.fueians.medicationapp.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fueians.medicationapp.view.screens.navigation.BottomNavItem
import com.fueians.medicationapp.view.screens.navigation.ScreenScaffold
import androidx.compose.runtime.Composable
import com.fueians.medicationapp.view.screens.ReportsScreen
import com.fueians.medicationapp.view.theme.AppTheme


class ReportsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                ReportsHost()
            }
        }
    }

    @Composable
    private fun ReportsHost() {
        ScreenScaffold(
            currentItem = BottomNavItem.Reports,
            onNavItemClick = { item ->
                when (item) {
                    is BottomNavItem.Home ->
                        startActivity(Intent(this, DashboardActivity::class.java))
                    is BottomNavItem.Medications ->
                        startActivity(Intent(this, MedicationListActivity::class.java))
                    is BottomNavItem.Schedule ->
                        startActivity(Intent(this, MedicationsScheduleActivity::class.java))
                    is BottomNavItem.Reports -> {}
                    is BottomNavItem.Profile ->
                        startActivity(Intent(this, ProfileActivity::class.java))
                }
            }
        ) {
            ReportsScreen()
        }
    }
}