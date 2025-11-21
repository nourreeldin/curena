package com.fueians.medicationapp.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fueians.medicationapp.view.screens.WelcomeScreen
import com.fueians.medicationapp.view.theme.AppTheme


class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.requestFeature(android.view.Window.FEATURE_NO_TITLE)
        setContent {
            AppTheme {
                WelcomeScreen(
                    onSignupClick = {
                    startActivity(Intent(this, SignupActivity::class.java))
                    finish()
                    },
                    onLoginClick = {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    }
                )
            }
        }
    }
}

