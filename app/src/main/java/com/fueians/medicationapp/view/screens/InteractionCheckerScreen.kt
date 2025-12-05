package com.fueians.medicationapp.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractionCheckerScreen(
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val showExampleDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Drug Interactions",
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

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // ===== Summary (Green) =====
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE6F6EC)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF22C55E)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "No Critical Interactions Detected",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color(0xFF166534)
                            )
                        }
                        Text(
                            text = "Your current medications have been checked for major interactions.",
                            fontSize = 13.sp,
                            color = Color(0xFF166534)
                        )
                    }
                }

                // ===== Detected Interactions title =====
                Text(
                    text = "Detected Interactions (2)",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = Color(0xFF111827)
                )

                // ===== Minor Interaction card =====
                InteractionLevelCard(
                    levelTitle = "MINOR INTERACTION",
                    levelColor = Color(0xFFEAB308),
                    containerColor = Color(0xFFFFF7D6),
                    pairText = "Metformin + Lisinopril",
                    description = "May increase risk of low blood sugar.",
                    recommendation = "Monitor blood glucose levels regularly."
                )

                // ===== Moderate Interaction card =====
                InteractionLevelCard(
                    levelTitle = "MODERATE INTERACTION",
                    levelColor = Color(0xFFF97316),
                    containerColor = Color(0xFFFFE7D6),
                    pairText = "Atorvastatin + Grapefruit juice",
                    description = "Grapefruit can increase medication levels in blood.",
                    recommendation = "Avoid grapefruit and grapefruit juice."
                )

                // ===== Severity Levels card =====
                SeverityLevelsCard()

                // ===== Info / tip card =====
                InfoTipCard()

                // ===== Button: View Example Interaction Alert =====
                Button(
                    onClick = { showExampleDialog.value = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text(
                        text = "View Example Interaction Alert",
                        color = Color(0xFF1D4ED8),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.height(24.dp))
            }

            // ===== Example Alert Dialog (custom) =====
            if (showExampleDialog.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .clickable(
                            indication = null,
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        ) { showExampleDialog.value = false }
                ) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            // Header row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFFE4E6)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = Color(0xFFDC2626)
                                        )
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Drug Interaction Alert",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = "MAJOR INTERACTION",
                                            color = Color(0xFFDC2626),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                IconButton(onClick = { showExampleDialog.value = false }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close"
                                    )
                                }
                            }

                            Text(
                                text = "Warfarin may interact with Aspirin.",
                                fontSize = 14.sp,
                                color = Color(0xFF111827)
                            )

                            // Risk box
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFFE4E6), RoundedCornerShape(12.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "Increased risk of bleeding when taken together.",
                                    color = Color(0xFFB91C1C),
                                    fontSize = 13.sp
                                )
                            }

                            // Recommendation box
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "Recommendation",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF111827)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Consult your doctor immediately before taking these medications together.",
                                    fontSize = 13.sp,
                                    color = Color(0xFF4B5563)
                                )
                            }

                            Spacer(Modifier.height(4.dp))

                            // Buttons
                            Button(
                                onClick = { showExampleDialog.value = false },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFDC2626)
                                )
                            ) {
                                Text(
                                    text = "I Understand",
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }

                            OutlinedButton(
                                onClick = { /* TODO: navigate to doctor/contact */ },
                                modifier = Modifier
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = "Contact Doctor",
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InteractionLevelCard(
    levelTitle: String,
    levelColor: Color,
    containerColor: Color,
    pairText: String,
    description: String,
    recommendation: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = levelTitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = levelColor
            )

            Text(
                text = pairText,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color(0xFF111827)
            )

            Text(
                text = description,
                fontSize = 13.sp,
                color = Color(0xFF4B5563)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "Recommendation",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = Color(0xFF111827)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = recommendation,
                    fontSize = 13.sp,
                    color = Color(0xFF4B5563)
                )
            }
        }
    }
}

@Composable
private fun SeverityLevelsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Severity Levels",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(4.dp))

            SeverityRow("Minor - Monitor carefully", Color(0xFFEAB308))
            SeverityRow("Moderate - May need adjustment", Color(0xFFF97316))
            SeverityRow("Major - Avoid combination", Color(0xFFDC2626))
            SeverityRow("Contraindicated - Do not use together", Color(0xFF6B21A8))
        }
    }
}

@Composable
private fun SeverityRow(
    text: String,
    dotColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = Color(0xFF4B5563)
        )
    }
}

@Composable
private fun InfoTipCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE5F0FF)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2563EB)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
            Text(
                text = "Always consult your doctor or pharmacist before making changes to your medication regimen.",
                fontSize = 13.sp,
                color = Color(0xFF1E3A8A)
            )
        }
    }
}
