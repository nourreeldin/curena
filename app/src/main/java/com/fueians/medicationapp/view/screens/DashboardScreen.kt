package com.fueians.medicationapp.view.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fueians.medicationapp.R

data class DashboardMedicationUi(
    val name: String,
    val dose: String,
    val nextTime: String,
    val cardColor: Color,
    val borderColor: Color,
    var statusIconRes: Int
)

@Composable
fun DashboardScreen(
    userFullName: String,
    todayMeds: List<DashboardMedicationUi>,
    onViewAllClick: () -> Unit,
    onAddMedicationClick: () -> Unit,
    onScheduleClick: () -> Unit,
    onReportsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onMedicationClick: (DashboardMedicationUi) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
    ) {

        // HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Text(
                        text = "Hello, $userFullName",
                        fontSize = 24.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Here's your health overview",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "Adherence",
                            value = "92%",
                            valueColor = Color(0xFF22C55E),
                            background = Color(0xFFEFFBF2),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Today's doses",
                            value = "3 / 5",
                            valueColor = Color.White,
                            background = Color(0xFF2563EB).copy(alpha = 0.18f),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Upcoming",
                            value = "2",
                            valueColor = Color(0xFFF97316),
                            background = Color(0xFFFFF4E9),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // TODAY'S MEDS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's Medications",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(onClick = onViewAllClick) {
                Text(
                    text = "View All",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            todayMeds.forEach { med ->
                MedicationCard(
                    med = med,
                    onClick = { onMedicationClick(med) }
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        // QUICK ACTIONS
        Text(
            text = "Quick Actions",
            modifier = Modifier.padding(horizontal = 20.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(10.dp))

        QuickActionsSection(
            onAdd = onAddMedicationClick,
            onSchedule = onScheduleClick,
            onReports = onReportsClick,
            onProfile = onProfileClick
        )

        Spacer(Modifier.height(16.dp))

        UpcomingReminderCard(
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    valueColor: Color,
    background: Color,
    modifier: Modifier
) {
    Card(
        modifier = modifier.height(70.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, fontSize = 11.sp, color = Color(0xFF6B7280))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

@Composable
private fun MedicationCard(
    med: DashboardMedicationUi,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = med.cardColor),
        border = BorderStroke(1.dp, med.borderColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.capsule),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(med.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text(med.dose, fontSize = 13.sp, color = Color(0xFF6B7280))
                Text("Next: ${med.nextTime}", fontSize = 12.sp, color = Color(0xFF9CA3AF))
            }

            Icon(
                painter = painterResource(id = med.statusIconRes),
                contentDescription = null,
                tint = med.borderColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun QuickActionsSection(
    onAdd: () -> Unit,
    onSchedule: () -> Unit,
    onReports: () -> Unit,
    onProfile: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickActionCard(
                title = "Add Medication",
                iconRes = R.drawable.plus,
                background = Color(0xFFE8F0FF),
                iconBg = MaterialTheme.colorScheme.primary,
                onClick = onAdd,
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                title = "Schedule",
                iconRes = R.drawable.calendar,
                background = Color(0xFFEAFBF0),
                iconBg = Color(0xFF22C55E),
                onClick = onSchedule,
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickActionCard(
                title = "Reports",
                iconRes = R.drawable.pdf,
                background = Color(0xFFF5ECFF),
                iconBg = Color(0xFF8B5CF6),
                onClick = onReports,
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                title = "Profile",
                iconRes = R.drawable.person,
                background = Color(0xFFFFF3E6),
                iconBg = Color(0xFFFF8A3D),
                onClick = onProfile,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    iconRes: Int,
    background: Color,
    iconBg: Color,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(background),
        elevation = CardDefaults.cardElevation(0.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun UpcomingReminderCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(Color(0xFFEFF2FF)),
        border = BorderStroke(1.dp, Color(0xFFD3DCFF)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.bell1),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.width(12.dp))

            Column {
                Text("Upcoming Reminder", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(
                    "Metformin 500mg at 2:00 PM",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}
