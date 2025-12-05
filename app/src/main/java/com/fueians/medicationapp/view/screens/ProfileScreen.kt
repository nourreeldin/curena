package com.fueians.medicationapp.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fueians.medicationapp.R

@Composable
fun ProfileScreen(
    userName: String = "John Doe",
    onNotificationSettingsClick: () -> Unit,
    onRefillTrackingClick: () -> Unit,
    onMissedDoseAlertsClick: () -> Unit,
    onCaregiverAccessClick: () -> Unit = {},
    onShareWithProviderClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 16.dp)
    ) {

        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {

            Column {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF93C5FD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.firstOrNull()?.uppercase() ?: "U",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Column {
                        Text(
                            text = userName,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Primary account",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Spacer(Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { /* TODO: edit profile */ },
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                width = 1.dp,
                                brush = SolidColor(Color.White)
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(50)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.edit),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text("Edit Profile", fontSize = 13.sp)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Stats cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    ProfileStatCard(
                        label = "Active Medications",
                        value = "5",
                        iconRes = R.drawable.capsule,
                        modifier = Modifier.weight(1f)
                    )

                    ProfileStatCard(
                        label = "Scheduled Doses",
                        value = "12",
                        iconRes = R.drawable.calendar,
                        modifier = Modifier.weight(1f)
                    )

                    ProfileStatCard(
                        label = "Adherence Rate",
                        value = "92%",
                        iconRes = R.drawable.heart,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Main sections
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            ProfileSection(
                title = "Personal",
                items = listOf(
                    ProfileItem("Edit Profile", "Update your personal information", R.drawable.edit),
                    ProfileItem("Health Trackers", "Track vitals and health metrics", R.drawable.heart),
                    ProfileItem("Appointments", "Manage medical appointments", R.drawable.calendar)
                )
            )

            ProfileSection(
                title = "Medications & Alerts",
                items = listOf(
                    ProfileItem("Notification Settings", "Manage reminders and alerts", R.drawable.bell),
                    ProfileItem("Refill Tracking", "Track prescription refills", R.drawable.capsule),
                    ProfileItem("Missed Dose Alerts", "View missed medications", R.drawable.bell)
                ),
                onItemClick = { item ->
                    when (item.title) {
                        "Notification Settings" -> onNotificationSettingsClick()
                        "Refill Tracking" -> onRefillTrackingClick()
                        "Missed Dose Alerts" -> onMissedDoseAlertsClick()
                    }
                }
            )

            ProfileSection(
                title = "Sharing & Access",
                items = listOf(
                    ProfileItem("Caregiver Access", "Manage caregiver permissions", R.drawable.person),
                    ProfileItem("Share with Provider", "Authorize providers", R.drawable.share)
                ),
                onItemClick = { item ->
                    when (item.title) {
                        "Caregiver Access" -> onCaregiverAccessClick()
                        "Share with Provider" -> onShareWithProviderClick()
                    }
                }
            )

            ProfileSection(
                title = "App Settings",
                items = listOf(
                    ProfileItem("Theme", "Light mode", R.drawable.themes),
                    ProfileItem("Language", "English", R.drawable.globe),
                    ProfileItem("Privacy & Security", "Manage privacy options", R.drawable.pdf)
                )
            )

            ProfileSection(
                title = "Support",
                items = listOf(
                    ProfileItem("Help & Support", "Get help & FAQs", R.drawable.bell),
                    ProfileItem("Terms & Privacy Policy", "Read policies", R.drawable.pdf)
                )
            )

            // Logout card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLogoutClick() },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.logout),
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Log Out",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = Color(0xFFEF4444)
                        )
                        Text(
                            text = "Sign out of your account",
                            fontSize = 13.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("MedManager v1.0.0", fontSize = 12.sp, color = Color(0xFF9CA3AF))
                Text("©️ 2025 All rights reserved", fontSize = 12.sp, color = Color(0xFF9CA3AF))
            }
        }
    }
}

// ----------------------------
//  DATA CLASSES & SUB COMPOSABLES
// ----------------------------

data class ProfileItem(
    val title: String,
    val subtitle: String,
    val iconRes: Int
)

@Composable
private fun ProfileStatCard(
    label: String,
    value: String,
    iconRes: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(90.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.18f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            Text(label, fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))
        }
    }
}

@Composable
private fun ProfileSection(
    title: String,
    items: List<ProfileItem>,
    onItemClick: (ProfileItem) -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {

        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF6B7280)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column {
                items.forEachIndexed { index, item ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onItemClick(item) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            painter = painterResource(id = item.iconRes),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF111827)
                            )
                            Text(
                                text = item.subtitle,
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280)
                            )
                        }

                        Icon(
                            painter = painterResource(id = R.drawable.next),
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (index != items.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.7.dp,
                            color = Color(0xFFE5E7EB)
                        )
                    }
                }
            }
        }
    }
}
