package com.fueians.medicationapp.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.fueians.medicationapp.view.screens.ProfileScreen
import com.fueians.medicationapp.view.screens.navigation.BottomNavItem
import com.fueians.medicationapp.view.screens.navigation.ScreenScaffold
import com.fueians.medicationapp.view.theme.AppTheme


class ProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userFullName = "amira"

        setContent {
            AppTheme {
                ProfileHost(userFullName)
            }
        }
    }

    @Composable
    private fun ProfileHost(userFullName: String) {
        ScreenScaffold(
            currentItem = BottomNavItem.Profile,
            onNavItemClick = { item ->
                when (item) {
                    is BottomNavItem.Home ->
                        startActivity(Intent(this@ProfileActivity, DashboardActivity::class.java))

                    is BottomNavItem.Medications ->
                        startActivity(Intent(this@ProfileActivity, MedicationListActivity::class.java))

                    is BottomNavItem.Schedule ->
                        startActivity(
                            Intent(
                                this@ProfileActivity,
                                MedicationsScheduleActivity::class.java
                            )
                        )

                    is BottomNavItem.Reports ->
                        startActivity(Intent(this@ProfileActivity, ReportsActivity::class.java))

                    is BottomNavItem.Profile -> {
                        // already here
                    }
                }
            }
        ) {
            ProfileScreen(
                userName = userFullName,
                onNotificationSettingsClick = {
                    startActivity(
                        Intent(
                            this@ProfileActivity,
                            NotificationSettingsActivity::class.java
                        )
                    )
                },
                onRefillTrackingClick = {
                    startActivity(
                        Intent(
                            this@ProfileActivity,
                            RefillTrackingActivity::class.java
                        )
                    )
                },
                onMissedDoseAlertsClick = {
                    startActivity(
                        Intent(
                            this@ProfileActivity,
                            MissedDoseAlertsActivity::class.java
                        )
                    )
                }
            )
        }
    }
}
