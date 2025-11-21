package com.fueians.medicationapp.view.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import com.fueians.medicationapp.view.screens.ForgotPasswordScreen
import com.fueians.medicationapp.view.theme.AppTheme

class ForgotPasswordActivity : ComponentActivity() {

    private var email = mutableStateOf("")
    private var isLoading = mutableStateOf(false)
    private var errorMessage = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.requestFeature(android.view.Window.FEATURE_NO_TITLE)
        setContent {
            AppTheme {
                ForgotPasswordScreen(
                    emailState = email.value,
                    onEmailChange = {
                        email.value = it
                        errorMessage.value = null
                    },
                    isLoading = isLoading.value,
                    errorMessage = errorMessage.value,
                    onSendResetClick = { handleSendReset() },
                    onNavigateBack = { finish() }
                )
            }
        }
    }

    private fun handleSendReset() {
        if (email.value.isBlank()) {
            errorMessage.value = "Email is required"
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
            errorMessage.value = "Invalid email format"
        } else {
            Toast.makeText(this, "Reset link sent (mock)", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
