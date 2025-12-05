package com.fueians.medicationapp.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.fueians.medicationapp.model.entities.MedicationEntity
import com.fueians.medicationapp.presenter.Medication.MedicationPresenter
import com.fueians.medicationapp.view.interfaces.IMedicationView
import com.fueians.medicationapp.view.screens.MedicationListItemUi
import com.fueians.medicationapp.view.screens.MedicationListScreen
import com.fueians.medicationapp.view.screens.navigation.BottomNavItem
import com.fueians.medicationapp.view.screens.navigation.ScreenScaffold
import com.fueians.medicationapp.view.theme.AppTheme

class MedicationListActivity : ComponentActivity(), IMedicationView {

    private lateinit var medicationPresenter: MedicationPresenter

    private val medications: SnapshotStateList<MedicationListItemUi> = mutableStateListOf()

    private val addMedicationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data ?: return@registerForActivityResult
                val name = data.getStringExtra("med_name") ?: return@registerForActivityResult
                val dosage = data.getStringExtra("med_dosage") ?: ""
                val frequency = data.getStringExtra("med_frequency") ?: ""

                medications.add(
                    MedicationListItemUi(
                        name = name,
                        dosage = dosage,
                        frequency = frequency
                    )
                )
            }
        }

    private val detailsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val deletedIndex = result.data?.getIntExtra("deleted_index", -1) ?: -1
                if (deletedIndex in medications.indices) {
                    medications.removeAt(deletedIndex)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = "user-id-placeholder"

        medicationPresenter = MedicationPresenter(this, applicationContext)
        medicationPresenter.attachView(this)
        medicationPresenter.loadMedications(userId)

        setContent {
            AppTheme {
                MedicationListHost()
            }
        }
    }

    @Composable
    private fun MedicationListHost() {
        ScreenScaffold(
            currentItem = BottomNavItem.Medications,
            onNavItemClick = { item ->
                when (item) {
                    is BottomNavItem.Home ->
                        startActivity(Intent(this@MedicationListActivity, DashboardActivity::class.java))
                    is BottomNavItem.Medications -> {}
                    is BottomNavItem.Schedule ->
                        startActivity(Intent(this@MedicationListActivity, MedicationsScheduleActivity::class.java))
                    is BottomNavItem.Reports ->
                        startActivity(Intent(this@MedicationListActivity, ReportsActivity::class.java))
                    is BottomNavItem.Profile ->
                        startActivity(Intent(this@MedicationListActivity, ProfileActivity::class.java))
                }
            }
        ) {
            MedicationListScreen(
                medications = medications,
                onAddMedicationClick = {
                    val intent = Intent(this@MedicationListActivity, AddMedicationActivity::class.java)
                    addMedicationLauncher.launch(intent)
                },
                onMedicationClick = { item ->
                    val index = medications.indexOf(item)
                    if (index == -1) return@MedicationListScreen

                    val intent = Intent(this@MedicationListActivity, MedicationDetailsActivity::class.java).apply {
                        putExtra("med_index", index)
                        putExtra("med_name", item.name)
                        putExtra("med_dosage", item.dosage)
                        putExtra("med_frequency", item.frequency)
                    }
                    detailsLauncher.launch(intent)
                },
                onSearchDrugInfoClick = {
                    val intent = Intent(this@MedicationListActivity, SearchDrugInfoActivity::class.java)
                    startActivity(intent)
                },
                onCheckInteractionsClick = {
                    val intent = Intent(this@MedicationListActivity, InteractionCheckerActivity::class.java)
                    startActivity(intent)
                }
            )
        }
    }

    // ----------------------
    // IMedicationView Methods
    // ----------------------
    override fun showLoading() {}
    override fun hideLoading() {}
    override fun displayMedications(medicationsList: List<MedicationEntity>) {
        medications.clear()
        medications.addAll(
            medicationsList.map { med ->
                MedicationListItemUi(
                    name = med.name,
                    dosage = med.dosage,
                    frequency = "Scheduled"
                )
            }
        )
    }

    override fun displayMedicationDetails(medication: MedicationEntity) {}
    override fun displayError(message: String) {}
    override fun onMedicationAdded() {}
    override fun onMedicationUpdated() {}
    override fun onMedicationDeleted() {}
    override fun displayInteractions(interactions: List<String>) {}
}
