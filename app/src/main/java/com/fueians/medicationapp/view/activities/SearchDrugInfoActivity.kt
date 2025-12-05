package com.fueians.medicationapp.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fueians.medicationapp.view.screens.SearchDrugInfoScreen
import com.fueians.medicationapp.view.screens.navigation.BottomNavItem
import com.fueians.medicationapp.view.screens.navigation.ScreenScaffold
import com.fueians.medicationapp.view.theme.AppTheme

class SearchDrugInfoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                 ScreenScaffold(
                     currentItem = BottomNavItem.Medications,
                     onNavItemClick = { item ->
                         when (item) {
                             is BottomNavItem.Home ->
                                 startActivity(
                                     Intent(this@SearchDrugInfoActivity, DashboardActivity::class.java)
                                 )

                             is BottomNavItem.Medications -> { /* already here */ }

                             is BottomNavItem.Schedule ->
                                 startActivity(
                                     Intent(this@SearchDrugInfoActivity, MedicationsScheduleActivity::class.java)
                                 )

                             is BottomNavItem.Reports ->
                                 startActivity(
                                     Intent(this@SearchDrugInfoActivity, ReportsActivity::class.java)
                                 )

                             is BottomNavItem.Profile ->
                                 startActivity(
                                     Intent(this@SearchDrugInfoActivity, ProfileActivity::class.java)
                                 )
                         }
                     }
                 ) {
                     SearchDrugInfoScreen(
                         onBackClick = { finish() }
                     )
                 }
             }
        }
    }
}
