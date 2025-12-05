package com.fueians.medicationapp.view.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fueians.medicationapp.view.screens.MissedDoseAlertsScreen
import com.fueians.medicationapp.view.theme.AppTheme


class MissedDoseAlertsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                MissedDoseAlertsScreen(
                    onBackClick = { finish() }
                )
            }
        }
    }
}
