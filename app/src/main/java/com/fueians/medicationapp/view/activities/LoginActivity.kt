package com.fueians.medicationapp.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import com.fueians.medicationapp.model.entities.UserEntity
//import com.fueians.medicationapp.presenter.LoginPresenter
import com.fueians.medicationapp.view.interfaces.ILoginView
import com.fueians.medicationapp.view.screens.LoginScreen
import com.fueians.medicationapp.view.theme.AppTheme

class LoginActivity : ComponentActivity(), ILoginView {

    //private lateinit var loginPresenter: LoginPresenter

    private var email = mutableStateOf("")
    private var password = mutableStateOf("")
    private var isLoading = mutableStateOf(false)
    private var errorMessage = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //loginPresenter = LoginPresenter(this)
        this.window.requestFeature(android.view.Window.FEATURE_NO_TITLE)
        setContent {
            AppTheme {
                LoginScreen(
                    emailState = email.value,
                    onEmailChange = {
                        email.value = it
                        errorMessage.value = null
                    },
                    passwordState = password.value,
                    onPasswordChange = {
                        password.value = it
                        errorMessage.value = null
                    },
                    isLoading = isLoading.value,
                    errorMessage = errorMessage.value,
                    onLoginClick = {
                        // loginPresenter.login(email.value, password.value)
                    },
                    onNavigateToSignup = {
                        startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
                    },
                    onForgotPassword = {
                        startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
                    }
                )
            }
        }
    }

    override fun showLoading() { isLoading.value = true }
    override fun hideLoading() { isLoading.value = false }

    override fun showLoginSuccess(user: UserEntity) {
        Toast.makeText(this, "Welcome ${user.email}", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun showLoginError(message: String) {
        errorMessage.value = message
        hideLoading()
    }

    override fun navigateToHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        //loginPresenter.detachView()
    }
}
