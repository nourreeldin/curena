package com.fueians.medicationapp.view.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.fueians.medicationapp.view.screens.RefillTrackingScreen
import com.fueians.medicationapp.view.screens.navigation.BottomNavItem
import com.fueians.medicationapp.view.screens.navigation.ScreenScaffold
import com.fueians.medicationapp.view.theme.AppTheme

class RefillTrackingActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                ScreenScaffold(
                    currentItem = BottomNavItem.Profile,
                    onNavItemClick = { item ->
                        when (item) {
                            is BottomNavItem.Home ->
                                startActivity(
                                    android.content.Intent(this, DashboardActivity::class.java)
                                )

                            is BottomNavItem.Medications ->
                                startActivity(
                                    android.content.Intent(this, MedicationListActivity::class.java)
                                )

                            is BottomNavItem.Schedule ->
                                startActivity(
                                    android.content.Intent(
                                        this,
                                        MedicationsScheduleActivity::class.java
                                    )
                                )

                            is BottomNavItem.Reports ->
                                startActivity(
                                    android.content.Intent(this, ReportsActivity::class.java)
                                )

                            is BottomNavItem.Profile -> {
                                // already here
                            }
                        }
                    }
                ) {
                    RefillTrackingScreen(
                        onBackClick = { finish() }
                    )
                }
            }
        }
    }
}
