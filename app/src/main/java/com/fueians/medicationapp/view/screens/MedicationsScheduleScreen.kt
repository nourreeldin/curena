package com.fueians.medicationapp.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fueians.medicationapp.R

data class MedicationScheduleItemUi(
    val name: String,
    val times: String,
    val frequency: String,
    val isEnabled: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationScheduleScreen(
    todaySummary: List<String>,
    schedules: List<MedicationScheduleItemUi>,
    onToggleSchedule: (index: Int, enabled: Boolean) -> Unit,
    onNewScheduleClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Medication Schedule",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    // ممكن تضيفي زر رجوع هنا لو حبيتي لاحقًا
                },
                actions = {
                    IconButton(onClick = { /* TODO: open schedule settings */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.settings),
                            contentDescription = "Settings",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewScheduleClick,
                containerColor = Color(0xFF1976D2),
                contentColor = Color.White
            ) {
                Text("+")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ---------- Today's Schedule ----------
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.calendar),
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Today's Schedule",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    todaySummary.forEach { line ->
                        Text(
                            text = line,
                            fontSize = 13.sp,
                            color = Color(0xFF4B5563)
                        )
                    }
                }
            }

            // ---------- All Schedules header ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "All Schedules",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFF4B5563)
                )
                TextButton(onClick = onNewScheduleClick) {
                    Text(
                        text = "+ New Schedule",
                        fontSize = 13.sp,
                        color = Color(0xFF1976D2)
                    )
                }
            }

            // ---------- Schedules list ----------
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                schedules.forEachIndexed { index, item ->
                    ScheduleCard(
                        item = item,
                        onToggle = { enabled -> onToggleSchedule(index, enabled) }
                    )
                }
            }

            Spacer(Modifier.height(60.dp))
        }
    }
}

@Composable
private fun ScheduleCard(
    item: MedicationScheduleItemUi,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF111827)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = item.times,
                    fontSize = 13.sp,
                    color = Color(0xFF4B5563)
                )
                Text(
                    text = item.frequency,
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF)
                )
            }

            Switch(
                checked = item.isEnabled,
                onCheckedChange = onToggle
            )
        }
    }
}
