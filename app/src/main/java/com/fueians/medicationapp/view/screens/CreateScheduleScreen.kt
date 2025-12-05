package com.fueians.medicationapp.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScheduleScreen(
    onBackClick: () -> Unit,
    onScheduleCreated: () -> Unit,
    initialMedicationTitle: String? = null
) {
    val medicationOptions = listOf(
        "Choose medication...",
        "Metformin 500mg",
        "Lisinopril 10mg",
        "Atorvastatin 20mg"
    )

    val defaultMedication = initialMedicationTitle
        ?.takeIf { it.isNotBlank() }
        ?: medicationOptions[0]

    var selectedMedication by remember { mutableStateOf(defaultMedication) }
    var medicationExpanded by remember { mutableStateOf(false) }

    val frequencyOptions = listOf(
        "Daily",
        "Weekly",
        "Custom Interval"
    )

    var selectedFrequency by remember { mutableStateOf(frequencyOptions[0]) }
    var frequencyExpanded by remember { mutableStateOf(false) }

    var reminderTime by remember { mutableStateOf("08:00 am") }
    var startDate by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create Schedule",
                        color = Color.White,
                        fontSize = 18.sp,
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
                actions = {
                    IconButton(onClick = { /* notification settings */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
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
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Select Medication
                Text(
                    text = "Select Medication",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4B5563)
                )

                ExposedDropdownMenuBox(
                    expanded = medicationExpanded,
                    onExpandedChange = { medicationExpanded = !medicationExpanded }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        value = selectedMedication,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Choose medication...") },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE1E4EC),
                            focusedBorderColor = Color(0xFF1976D2)
                        ),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = medicationExpanded)
                        },
                        singleLine = true
                    )

                    ExposedDropdownMenu(
                        expanded = medicationExpanded,
                        onDismissRequest = { medicationExpanded = false }
                    ) {
                        medicationOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedMedication = option
                                    medicationExpanded = false
                                }
                            )
                        }
                    }
                }

                // Frequency
                Text(
                    text = "Frequency",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4B5563)
                )

                ExposedDropdownMenuBox(
                    expanded = frequencyExpanded,
                    onExpandedChange = { frequencyExpanded = !frequencyExpanded }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        value = selectedFrequency,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Daily") },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE1E4EC),
                            focusedBorderColor = Color(0xFF1976D2)
                        ),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = frequencyExpanded)
                        },
                        singleLine = true
                    )

                    ExposedDropdownMenu(
                        expanded = frequencyExpanded,
                        onDismissRequest = { frequencyExpanded = false }
                    ) {
                        frequencyOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedFrequency = option
                                    frequencyExpanded = false
                                }
                            )
                        }
                    }
                }

                // Reminder time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Reminder Times",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4B5563)
                    )
                    TextButton(onClick = { /* add another time */ }) {
                        Text(
                            text = "+ Add Time",
                            fontSize = 13.sp,
                            color = Color(0xFF1976D2)
                        )
                    }
                }

                OutlinedTextField(
                    value = reminderTime,
                    onValueChange = { reminderTime = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE1E4EC),
                        focusedBorderColor = Color(0xFF1976D2)
                    ),
                    singleLine = true
                )

                // Start date
                Text(
                    text = "Start Date",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4B5563)
                )

                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    placeholder = { Text("dd/mm/yyyy") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE1E4EC),
                        focusedBorderColor = Color(0xFF1976D2)
                    ),
                    singleLine = true
                )

                // Info card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE6F0FF)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "You can customize notification settings including sound, vibration, and quiet hours in the notification settings.",
                            fontSize = 12.sp,
                            color = Color(0xFF1F2933)
                        )
                    }
                }
            }

            Button(
                onClick = {
                    // TODO: validation + save
                    onScheduleCreated()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1976D2),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Create Schedule",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
