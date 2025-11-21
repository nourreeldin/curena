package com.fueians.medicationapp.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fueians.medicationapp.view.screens.EmailVerificationScreen
import com.fueians.medicationapp.view.theme.AppTheme

class EmailVerificationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.requestFeature(android.view.Window.FEATURE_NO_TITLE)
        val email = intent.getStringExtra("email") ?: "your email"

        setContent {
            AppTheme {
                EmailVerificationScreen(
                    email = email,
                    onContinueToLogin = {
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    },
                    onResendClick = {
                        // TODO: call presenter/repo to resend
                    }
                )
            }
        }
    }
}
