package com.fueians.medicationapp.view.activities
import SplashPresenter
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.fueians.medicationapp.model.clients.SupabaseClient
import com.fueians.medicationapp.view.interfaces.ISplashView
import com.fueians.medicationapp.view.screens.WelcomeScreen
import com.fueians.medicationapp.view.theme.AppTheme
/**
 * MainActivity serves as the application's primary entry point for UI rendering.
 *
 * Notes for developers:
 * - This activity uses Jetpack Compose to display UI content.
 * - Wrap all screens with `AppTheme` for consistent styling.
 * - Avoid placing business logic here; delegate it to Presenters or ViewModels.
 * - In future implementations, navigation to AuthActivity or DashboardActivity can be handled here.
 */
class MainActivity : ComponentActivity(), ISplashView {
    private lateinit var presenter: SplashPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashscreen = installSplashScreen()
        presenter = SplashPresenter(this)
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

    private var keepSplash = true

    override fun showMainScreen() {
        keepSplash = false
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}
