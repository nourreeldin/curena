package com.fueians.medicationapp.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fueians.medicationapp.R

data class MissedDoseUi(
    val medication: String,
    val time: String,
    val date: String,
    val reason: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissedDoseAlertsScreen(
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    val missedDoses = listOf(
        MissedDoseUi("Lisinopril 10mg", "10:00 AM", "Today", "You marked this dose as missed."),
        MissedDoseUi("Metformin 500mg", "8:00 AM", "Yesterday", "No response to reminder."),
        MissedDoseUi("Atorvastatin 20mg", "9:00 PM", "2 days ago", "Reminder was snoozed.")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Missed Dose Alerts",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Summary card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF4E6))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Color(0xFFF97316),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            "You have ${missedDoses.size} recent missed doses",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF7C2D12)
                        )
                        Text(
                            "Try to review and update your schedule if needed.",
                            fontSize = 13.sp,
                            color = Color(0xFF9A3412)
                        )
                    }
                }
            }

            // List of missed doses
            missedDoses.forEach { item ->
                MissedDoseCard(item)
            }

            Spacer(Modifier.height(8.dp))

            InfoTipCard(
                text = "If you frequently miss doses, consider adjusting reminder times " +
                        "or enabling additional notifications in Notification Settings."
            )
        }
    }
}

@Composable
private fun MissedDoseCard(item: MissedDoseUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.bell), // 🔔 بدل Error
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = item.medication,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                "${item.time} • ${item.date}",
                fontSize = 13.sp,
                color = Color(0xFF6B7280)
            )
            Text(
                item.reason,
                fontSize = 13.sp,
                color = Color(0xFF374151)
            )
        }
    }
}

@Composable
private fun InfoTipCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE5F0FF))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(12.dp),
            fontSize = 13.sp,
            color = Color(0xFF1F2933)
        )
    }
}
