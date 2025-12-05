package com.fueians.medicationapp.view.screens.navigation

import androidx.annotation.DrawableRes
import com.fueians.medicationapp.R

sealed class BottomNavItem(
    val route: String,
    val label: String,
    @DrawableRes val iconRes: Int
) {
    object Home : BottomNavItem("home", "Home", R.drawable.home)
    object Medications : BottomNavItem("medications", "Medications", R.drawable.capsule)
    object Schedule : BottomNavItem("schedule", "Schedule", R.drawable.calendar)
    object Reports : BottomNavItem("reports", "Reports", R.drawable.pdf)
    object Profile : BottomNavItem("profile", "Profile", R.drawable.person)
}
