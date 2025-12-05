package com.fueians.medicationapp.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fueians.medicationapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onBackClick: () -> Unit
) {
    var enabled by remember { mutableStateOf(true) }
    var doseReminders by remember { mutableStateOf(true) }
    var refillReminders by remember { mutableStateOf(true) }
    var quietHours by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notification Settings",
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

            SettingSwitchCard(
                title = "Enable Notifications",
                subtitle = "Turn all app notifications on or off",
                checked = enabled,
                onCheckedChange = { enabled = it },
                iconRes = R.drawable.bell,
                activeTint = Color(0xFF22C55E)
            )

            SettingSwitchCard(
                title = "Dose Reminders",
                subtitle = "Remind you at scheduled medication times",
                checked = doseReminders,
                onCheckedChange = { doseReminders = it },
                iconRes = R.drawable.calendar,
                activeTint = Color(0xFF2563EB)
            )

            SettingSwitchCard(
                title = "Refill Reminders",
                subtitle = "Notify you when supplies are running low",
                checked = refillReminders,
                onCheckedChange = { refillReminders = it },
                iconRes = R.drawable.capsule,
                activeTint = Color(0xFFF97316)
            )

            SettingSwitchCard(
                title = "Quiet Hours",
                subtitle = "Mute notifications during selected hours",
                checked = quietHours,
                onCheckedChange = { quietHours = it },
                iconRes = R.drawable.bell,
                activeTint = Color(0xFF6B7280)
            )

            Spacer(Modifier.height(12.dp))

            InfoTipCard(
                text = "You can still see reminders in the app even when notifications are muted."
            )
        }
    }
}

@Composable
private fun SettingSwitchCard(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    iconRes: Int,
    activeTint: Color
) {
    val iconTint = if (checked) activeTint else Color(0xFF9CA3AF)

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Text(subtitle, fontSize = 13.sp, color = Color(0xFF6B7280))
            }

            // ✅ Switch عادي من Material3
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
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
