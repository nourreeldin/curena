package com.fueians.medicationapp.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import com.fueians.medicationapp.presenter.SignupPresenter
import com.fueians.medicationapp.view.interfaces.ISignupView
import com.fueians.medicationapp.view.screens.SignupScreen
import com.fueians.medicationapp.view.theme.AppTheme

class SignupActivity : ComponentActivity(), ISignupView {

    private lateinit var presenter: SignupPresenter

    private var fullName = mutableStateOf("")
    private var email = mutableStateOf("")
    private var password = mutableStateOf("")
    private var confirmPassword = mutableStateOf("")
    private var isLoading = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.requestFeature(android.view.Window.FEATURE_NO_TITLE)

        presenter = SignupPresenter(this, applicationContext)

        setContent {
            AppTheme {
                SignupScreen(
                    nameState = fullName.value,
                    onNameChange = { fullName.value = it },

                    emailState = email.value,
                    onEmailChange = { email.value = it },

                    passwordState = password.value,
                    onPasswordChange = { password.value = it },

                    confirmPasswordState = confirmPassword.value,
                    onConfirmPasswordChange = { confirmPassword.value = it },

                    onRegisterClick = {
                        presenter.register(
                            fullName.value,
                            email.value,
                            password.value,
                            confirmPassword.value
                        )
                    }
                )
            }
        }
    }

    override fun showLoading() { isLoading.value = true }

    override fun hideLoading() { isLoading.value = false }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showRegistrationSuccess() {
        startActivity(Intent(this, EmailVerificationActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}
