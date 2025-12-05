package com.fueians.medicationapp.view.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fueians.medicationapp.model.entities.MedicationEntity
import com.fueians.medicationapp.presenter.Medication.MedicationPresenter
import com.fueians.medicationapp.view.interfaces.IMedicationView
import com.fueians.medicationapp.view.screens.AddMedicationScreen
import com.fueians.medicationapp.view.theme.AppTheme
import java.util.*

class AddMedicationActivity : ComponentActivity(), IMedicationView {

    private lateinit var medicationPresenter: MedicationPresenter
    private val currentUserId = "user-id-placeholder"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize presenter
        medicationPresenter = MedicationPresenter(this, applicationContext)
        medicationPresenter.attachView(this)

        setContent {
            AppTheme {
                AddMedicationScreen(
                    onBackClick = { finish() },
                    onSaveClick = { name, dosage, frequency, instructions ->
                        // Create MedicationEntity
                        val medication = MedicationEntity(
                            id = UUID.randomUUID().toString(),
                            userId = currentUserId,
                            name = name,
                            dosage = dosage,
                            frequency = frequency,
                            instructions = instructions,
                            startDate = System.currentTimeMillis() // set current time as start
                        )

                        // Add medication via presenter
                        medicationPresenter.addMedication(medication)
                    }
                )
            }
        }
    }

    // ----------------------
    // IMedicationView Methods
    // ----------------------
    override fun showLoading() { /* optional: show loading UI */ }
    override fun hideLoading() { /* optional: hide loading UI */ }
    override fun displayMedications(medications: List<MedicationEntity>) { /* not needed here */ }
    override fun displayMedicationDetails(medication: MedicationEntity) { /* not needed here */ }
    override fun displayError(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
    override fun onMedicationAdded() {
        setResult(RESULT_OK)
        finish()
    }
    override fun onMedicationUpdated() { /* not needed here */ }
    override fun onMedicationDeleted() { /* not needed here */ }
    override fun displayInteractions(interactions: List<String>) { /* not needed here */ }

    override fun onDestroy() {
        super.onDestroy()
        medicationPresenter.detachView()
    }
}
