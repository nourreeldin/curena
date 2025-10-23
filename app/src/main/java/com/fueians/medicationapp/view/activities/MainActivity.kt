package com.fueians.medicationapp.view.activities
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // TODO: In future — attach Presenter here if following MVP
        // Example: mainPresenter = MainPresenter(this)

        setContent {
            AppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    GreetingScreen(
                        name = "Medication App",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
@Composable
fun GreetingScreen(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Welcome to $name!",
        modifier = modifier
    )
}
@Preview(showBackground = true)
@Composable
fun GreetingScreenPreview() {
    AppTheme {
        GreetingScreen("Medication App")
    }
}
