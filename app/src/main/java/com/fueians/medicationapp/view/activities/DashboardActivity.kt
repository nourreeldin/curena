package com.fueians.medicationapp.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.fueians.medicationapp.R
import com.fueians.medicationapp.model.entities.MedicationEntity
import com.fueians.medicationapp.model.entities.MedicationScheduleEntity
import com.fueians.medicationapp.presenter.Medication.MedicationPresenter
import com.fueians.medicationapp.presenter.Schedule.SchedulePresenter
import com.fueians.medicationapp.view.interfaces.IMedicationView
import com.fueians.medicationapp.view.interfaces.IScheduleView
import com.fueians.medicationapp.view.screens.DashboardMedicationUi
import com.fueians.medicationapp.view.screens.DashboardScreen
import com.fueians.medicationapp.view.screens.navigation.BottomNavItem
import com.fueians.medicationapp.view.screens.navigation.ScreenScaffold
import com.fueians.medicationapp.view.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : ComponentActivity(), IMedicationView, IScheduleView {

    private lateinit var medicationPresenter: MedicationPresenter
    private lateinit var schedulePresenter: SchedulePresenter

    private val todayMedsState = mutableStateListOf<DashboardMedicationUi>()
    private val schedulesMap = mutableStateMapOf<String, List<MedicationScheduleEntity>>()
    private val isLoading = mutableStateOf(false)
    private val errorMessage = mutableStateOf<String?>(null)

    private val userId = "user-id-placeholder" // Replace with real user ID
    private val userFullName = "amira"          // Replace with actual user name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        medicationPresenter = MedicationPresenter(this, applicationContext)
        schedulePresenter = SchedulePresenter(this, applicationContext)

        medicationPresenter.attachView(this)
        schedulePresenter.attachView(this)

        setContent {
            AppTheme {
                DashboardHost(userFullName)
            }
        }

        // Load data
        medicationPresenter.loadMedications(userId)
        schedulePresenter.loadSchedules()
        schedulePresenter.loadMissedDoses()
    }

    @Composable
    private fun DashboardHost(userFullName: String) {
        ScreenScaffold(
            currentItem = BottomNavItem.Home,
            onNavItemClick = { item ->
                when (item) {
                    is BottomNavItem.Home -> { /* Already here */ }
                    is BottomNavItem.Medications ->
                        startActivity(Intent(this@DashboardActivity, MedicationListActivity::class.java))
                    is BottomNavItem.Schedule ->
                        startActivity(Intent(this@DashboardActivity, MedicationsScheduleActivity::class.java))
                    is BottomNavItem.Reports ->
                        startActivity(Intent(this@DashboardActivity, ReportsActivity::class.java))
                    is BottomNavItem.Profile ->
                        startActivity(Intent(this@DashboardActivity, ProfileActivity::class.java))
                }
            }
        ) {
            DashboardScreen(
                userFullName = userFullName,
                todayMeds = todayMedsState,
                onViewAllClick = { startActivity(Intent(this@DashboardActivity, MedicationListActivity::class.java)) },
                onAddMedicationClick = { startActivity(Intent(this@DashboardActivity, AddMedicationActivity::class.java)) },
                onScheduleClick = { startActivity(Intent(this@DashboardActivity, MedicationsScheduleActivity::class.java)) },
                onReportsClick = { startActivity(Intent(this@DashboardActivity, ReportsActivity::class.java)) },
                onProfileClick = { startActivity(Intent(this@DashboardActivity, ProfileActivity::class.java)) },
                onMedicationClick = { med ->
                    val intent = Intent(this@DashboardActivity, MedicationDetailsActivity::class.java)
                    intent.putExtra("med_name", med.name)
                    intent.putExtra("med_dosage", med.dose)
                    startActivity(intent)
                }
            )
        }
    }

    // ----------------------
    // IMedicationView
    // ----------------------
    override fun showLoading() { isLoading.value = true }
    override fun hideLoading() { isLoading.value = false }

    override fun displayMedications(medications: List<MedicationEntity>) {
        todayMedsState.clear()
        todayMedsState.addAll(
            medications.map { med ->
                val schedules = schedulesMap[med.id] ?: emptyList()
                val nextSchedule = schedules
                    .filter { it.scheduledTime >= System.currentTimeMillis() }
                    .minByOrNull { it.scheduledTime }

                val nextTimeText = nextSchedule?.let { formatTime(it.scheduledTime) } ?: "Scheduled"
                val statusIcon = nextSchedule?.let {
                    if (it.isMissed()) R.drawable.bell else R.drawable.check
                } ?: R.drawable.bell

                DashboardMedicationUi(
                    name = med.name,
                    dose = med.dosage,
                    nextTime = nextTimeText,
                    cardColor = Color(0xFFEFF2FF),
                    borderColor = Color(0xFFCBD5FF),
                    statusIconRes = statusIcon
                )
            }
        )
    }

    override fun displayMedicationDetails(medication: MedicationEntity) {}
    override fun displayInteractions(interactions: List<String>) {}
    override fun onMedicationAdded() { medicationPresenter.loadMedications(userId) }
    override fun onMedicationUpdated() { medicationPresenter.loadMedications(userId) }
    override fun onMedicationDeleted() { medicationPresenter.loadMedications(userId) }
    override fun displayError(message: String) { errorMessage.value = message }

    // ----------------------
    // IScheduleView
    // ----------------------
    override fun displaySchedules(schedules: List<MedicationScheduleEntity>) {
        schedulesMap.clear()
        schedules.groupBy { it.medicationId }.forEach { (medId, scheduleList) ->
            schedulesMap[medId] = scheduleList
        }
        // Refresh medications UI after receiving schedules
        medicationPresenter.loadMedications(userId)
    }

    override fun displayMissedDoses(doses: List<MedicationScheduleEntity>) {
        doses.forEach { dose ->
            // Optional: Highlight missed doses in UI
            val med = todayMedsState.find { it.name == dose.medicationId }
            med?.let { it.statusIconRes = R.drawable.bell }
        }
    }

    override fun onScheduleCreated() { schedulePresenter.loadSchedules() }
    override fun onScheduleUpdated() { schedulePresenter.loadSchedules() }
    override fun onScheduleDeleted() { schedulePresenter.loadSchedules() }

    override fun onDestroy() {
        medicationPresenter.detachView()
        schedulePresenter.detachView()
        super.onDestroy()
    }

    // ----------------------
    // Helpers
    // ----------------------
    private fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun MedicationScheduleEntity.isMissed(): Boolean {
        return scheduledTime < System.currentTimeMillis()
    }
}
