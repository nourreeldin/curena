package com.fueians.medicationapp.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.fueians.medicationapp.view.screens.CreateScheduleScreen
import com.fueians.medicationapp.view.screens.navigation.BottomNavItem
import com.fueians.medicationapp.view.screens.navigation.ScreenScaffold
import com.fueians.medicationapp.view.theme.AppTheme

class CreateScheduleActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val initialMedicationTitle = intent.getStringExtra("med_title")

        setContent {
            AppTheme {
                CreateScheduleHost(initialMedicationTitle = initialMedicationTitle)
            }
        }
    }

    @Composable
    private fun CreateScheduleHost(initialMedicationTitle: String?) {
        ScreenScaffold(
            currentItem = BottomNavItem.Schedule,
            onNavItemClick = { item ->
                when (item) {
                    is BottomNavItem.Home ->
                        startActivity(
                            Intent(this@CreateScheduleActivity, DashboardActivity::class.java)
                        )

                    is BottomNavItem.Medications ->
                        startActivity(
                            Intent(this@CreateScheduleActivity, MedicationListActivity::class.java)
                        )

                    is BottomNavItem.Schedule -> {

                        startActivity(
                            Intent(
                                this@CreateScheduleActivity,
                                MedicationsScheduleActivity::class.java
                            )
                        )
                    }

                    is BottomNavItem.Reports ->
                        startActivity(
                            Intent(this@CreateScheduleActivity, ReportsActivity::class.java)
                        )

                    is BottomNavItem.Profile ->
                        startActivity(
                            Intent(this@CreateScheduleActivity, ProfileActivity::class.java)
                        )
                }
            }
        ) {
            CreateScheduleScreen(
                onBackClick = { finish() },
                onScheduleCreated = {

                    startActivity(
                        Intent(
                            this@CreateScheduleActivity,
                            MedicationsScheduleActivity::class.java
                        )
                    )
                    finish()
                },
                initialMedicationTitle = initialMedicationTitle
            )
        }
    }
}
