package com.fueians.medicationapp.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun ReportsScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

        // Header
        Text(
            text = "Reports & Analytics",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B1B1B)
        )

        Spacer(Modifier.height(16.dp))

        // Top stat cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ReportStatCard(
                title = "Adherence Rate",
                value = "92%",
                background = MaterialTheme.colorScheme.primary,
                icon = R.drawable.heart,
                modifier = Modifier.weight(1f)
            )
            ReportStatCard(
                title = "Total Doses",
                value = "248",
                background = Color(0xFF22C55E),
                icon = R.drawable.capsule,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(18.dp))

        // Report period
        Text("Report Period", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DateBox("Start Date", "01/01/2024", Modifier.weight(1f))
            DateBox("End Date", "31/01/2024", Modifier.weight(1f))
        }

        Spacer(Modifier.height(18.dp))

        // Report type
        Text("Report Type", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .background(Color.White, RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Medication Adherence",
                    fontSize = 14.sp,
                    color = Color(0xFF1B1B1B),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = painterResource(id = R.drawable.more),
                    contentDescription = null,
                    tint = Color(0xFF6B7280),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        // Report preview
        Text("Report Preview", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(18.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            RowJustify("Doses Taken", "228/248")
            RowJustify("Doses Missed", "20", rightColor = Color(0xFFDC2626))
            RowJustify("On-Time Rate", "89%", rightColor = Color(0xFF16A34A))

            Spacer(Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(Color(0xFFE5E7EB), RoundedCornerShape(50)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.89f)
                        .fillMaxHeight()
                        .background(Color(0xFF22C55E), RoundedCornerShape(50))
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        // By medication section
        Text("By Medication", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(18.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MedicationProgressRow("Metformin", "58/61", 0.9f)
            MedicationProgressRow("Lisinopril", "27/30", 0.85f)
            MedicationProgressRow("Atorvastatin", "26/30", 0.8f)
        }

        Spacer(Modifier.height(20.dp))

        // Generate report button
        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.pdf),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Generate Full Report", fontSize = 16.sp)
        }

        Spacer(Modifier.height(12.dp))

        // Export + share
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            OutlinedButton(
                onClick = { },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.pdf),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Export PDF")
            }

            OutlinedButton(
                onClick = { },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.share),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Share")
            }
        }
    }
}

// ----------------------------
// SUB COMPOSABLES
// ----------------------------

@Composable
private fun ReportStatCard(
    title: String,
    value: String,
    background: Color,
    icon: Int,
    modifier: Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(text = title, fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f))
                Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun DateBox(label: String, value: String, modifier: Modifier) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 13.sp, color = Color(0xFF6B7280))
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .background(Color.White, RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(text = value, fontSize = 14.sp, color = Color(0xFF111827))
        }
    }
}

@Composable
private fun RowJustify(left: String, right: String, rightColor: Color = Color(0xFF111827)) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(left, fontSize = 14.sp, color = Color(0xFF4B5563))
        Text(right, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = rightColor)
    }
}

@Composable
private fun MedicationProgressRow(name: String, value: String, progress: Float) {
    Column {
        RowJustify(name, value)
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(Color(0xFFE5E7EB), RoundedCornerShape(50))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
            )
        }
    }
}
