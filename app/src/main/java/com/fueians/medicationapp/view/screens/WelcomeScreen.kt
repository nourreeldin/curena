package com.fueians.medicationapp.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fueians.medicationapp.R
import com.fueians.medicationapp.view.theme.AppThemeState

@Composable
fun WelcomeScreen(
    onSignupClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    var page by remember { mutableStateOf(0) }

    val pages = listOf(
        WelcomePageData(
            title = "Manage Your Medications",
            description = "Keep track of all your medications in one secure place",
            iconRes = R.drawable.pill
        ),
        WelcomePageData(
            title = "Never Miss a Dose",
            description = "Get timely reminders for all your medications",
            iconRes = R.drawable.bell
        ),
        WelcomePageData(
            title = "Stay Safe & Informed",
            description = "Check drug interactions and access full info",
            iconRes = R.drawable.secure
        ),
        WelcomePageData(
            title = "Share with Caregivers",
            description = "Allow caregivers & doctors to monitor your meds",
            iconRes = R.drawable.caregiver
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {

        //  Theme toggle
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (AppThemeState.isDark) "Dark" else "Light",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.width(4.dp))
            Switch(
                checked = AppThemeState.isDark,
                onCheckedChange = { AppThemeState.isDark = it }
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.9f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(id = pages[page].iconRes),
                contentDescription = pages[page].title,
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = pages[page].title,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = pages[page].description,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pages.size) { index ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (index == page) 14.dp else 8.dp)
                            .background(
                                if (index == page)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outline,
                                CircleShape
                            )
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Button(
                onClick = {
                    if (page < pages.size - 1) {
                        page++
                    } else {
                        onSignupClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = if (page == pages.size - 1) "Get Started" else "Next",
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (page == pages.size - 1) {
                OutlinedButton(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    border = ButtonDefaults.outlinedButtonBorder,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("I already have an account")
                }
            } else {
                TextButton(
                    onClick = onLoginClick,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        "Skip",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

data class WelcomePageData(
    val title: String,
    val description: String,
    val iconRes: Int
)
