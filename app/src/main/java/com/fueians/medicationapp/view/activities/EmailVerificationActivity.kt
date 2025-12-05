package com.fueians.medicationapp.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fueians.medicationapp.presenter.EmailVerification.EmailVerificationPresenter
import com.fueians.medicationapp.view.interfaces.IEmailVerificationView
import com.fueians.medicationapp.view.screens.EmailVerificationScreen
import com.fueians.medicationapp.view.theme.AppTheme

class EmailVerificationActivity : ComponentActivity(), IEmailVerificationView {

    private lateinit var presenter: EmailVerificationPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.requestFeature(android.view.Window.FEATURE_NO_TITLE)
        val email = intent.getStringExtra("email") ?: "your email"
        presenter = EmailVerificationPresenter(this)
        presenter.attachView(this)
        setContent {
            AppTheme {
                EmailVerificationScreen(
                    email = email,
                    onContinueToLogin = {
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    },
                    onResendClick = {
                        presenter.resendCode(email)
                    }
                )
            }
        }
    }

    // ---------------- IEmailVerificationView implementation ----------------

    override fun onEmailSentSuccess() {
        Toast.makeText(this, "Verification email sent!", Toast.LENGTH_SHORT).show()
    }

    override fun onVerificationSuccess(userId: String) {
        Toast.makeText(this, "Verification successful!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun showVerificationError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showLoading() {
        // Screen has no loading state
    }

    override fun hideLoading() {
        // Screen has no loading state
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}
