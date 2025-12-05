package com.fueians.medicationapp.view.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import com.fueians.medicationapp.view.interfaces.IForgotPasswordView
import com.fueians.medicationapp.view.screens.ForgotPasswordScreen
import com.fueians.medicationapp.view.theme.AppTheme

class ForgotPasswordActivity : ComponentActivity(), IForgotPasswordView {

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
            showValidationError("Email is required")
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
            showValidationError("Invalid email format")
        } else {
            showLoading()
            // Simulate sending email (mock)
            Toast.makeText(this, "Reset link sent (mock)", Toast.LENGTH_SHORT).show()
            onResetRequestSuccess()
            hideLoading()
        }
    }

    // ---------------- IForgotPasswordView implementation ----------------

    override fun showLoading() {
        isLoading.value = true
    }

    override fun hideLoading() {
        isLoading.value = false
    }

    override fun onResetRequestSuccess() {
        Toast.makeText(this, "Password reset request successful", Toast.LENGTH_SHORT).show()
        // Optionally finish activity or stay for code input
    }

    override fun onPasswordResetSuccess() {
        Toast.makeText(this, "Password reset successful", Toast.LENGTH_SHORT).show()
        finish() // Close activity after success
    }

    override fun showValidationError(message: String) {
        errorMessage.value = message
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showErrorMessage(message: String) {
        errorMessage.value = message
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
