package com.fueians.medicationapp.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fueians.medicationapp.view.screens.MedicationDetailsScreen
import com.fueians.medicationapp.view.screens.navigation.BottomNavItem
import com.fueians.medicationapp.view.screens.navigation.ScreenScaffold
import com.fueians.medicationapp.view.theme.AppTheme

class MedicationDetailsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val medIndex = intent.getIntExtra("med_index", -1)
        val name = intent.getStringExtra("med_name") ?: "Medication"
        val dosage = intent.getStringExtra("med_dosage") ?: ""
        val generic = intent.getStringExtra("med_generic") ?: ""

        val medTitleForSchedule = "$name $dosage".trim()

        setContent {
            AppTheme {
                ScreenScaffold(
                    currentItem = BottomNavItem.Medications,
                    onNavItemClick = {  }
                ) {
                    MedicationDetailsScreen(
                        name = name,
                        dosage = dosage,
                        genericName = generic,
                        onBackClick = { finish() },
                        onEditClick = { /* TODO: edit later */ },
                        onDeleteClick = {
                            if (medIndex != -1) {
                                val result = Intent().apply {
                                    putExtra("deleted_index", medIndex)
                                }
                                setResult(RESULT_OK, result)
                            }
                            finish()
                        },
                        onSetScheduleClick = {

                            val intent = Intent(
                                this@MedicationDetailsActivity,
                                CreateScheduleActivity::class.java
                            ).apply {
                                putExtra("med_title", medTitleForSchedule)
                            }
                            startActivity(intent)
                        },
                        onTrackRefillsClick = {

                            val intent = Intent(
                                this@MedicationDetailsActivity,
                                RefillTrackingActivity::class.java
                            )
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}
