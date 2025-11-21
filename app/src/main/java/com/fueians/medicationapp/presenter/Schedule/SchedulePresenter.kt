package com.fueians.medicationapp.presenter.Schedule

import com.fueians.medicationapp.presenter.TestRepo.AdherenceLog
import com.fueians.medicationapp.presenter.TestRepo.MedicationSchedule
import com.fueians.medicationapp.presenter.TestRepo.NotificationSettings
import com.fueians.medicationapp.presenter.TestRepo.ScheduleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// =========================================================================
// 1. Placeholder Service & View Interface
// =========================================================================

class ReminderService {
    fun setReminder(schedule: MedicationSchedule) {
        println("Reminder set for schedule: ${schedule.id}")
    }

    fun cancelReminder(scheduleId: String) {
        println("Reminder canceled for schedule: $scheduleId")
    }
}

interface IScheduleView {
    fun showLoading()
    fun hideLoading()
    fun displaySchedules(schedules: List<MedicationSchedule>)
    fun displayMissedDoses(doses: List<AdherenceLog>)
    fun displayError(message: String)
    fun onScheduleCreated()
    fun onScheduleUpdated()
    fun onScheduleDeleted()
}

// =========================================================================
// 2. Presenter
// =========================================================================

class SchedulePresenter(
    private var view: IScheduleView?,
    private val scheduleRepository: ScheduleRepository,
    private val reminderService: ReminderService = ReminderService()
) {

    private val presenterScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun attachView(view: IScheduleView) {
        this.view = view
    }

    fun detachView() {
        view = null
        presenterScope.cancel()
    }

    fun loadSchedules() {
        view?.showLoading()
        presenterScope.launch {
            scheduleRepository.loadSchedules().collectLatest {
                view?.displaySchedules(it)
                view?.hideLoading()
            }
        }
    }

    fun loadTodaySchedules() {
        view?.showLoading()
        presenterScope.launch {
            scheduleRepository.loadTodaySchedules().collectLatest {
                view?.displaySchedules(it)
                view?.hideLoading()
            }
        }
    }

    fun createSchedule(schedule: MedicationSchedule) {
        view?.showLoading()
        presenterScope.launch {
            try {
                scheduleRepository.createSchedule(schedule)
                if (schedule.isRecurring) {
                    reminderService.setReminder(schedule)
                }
                view?.hideLoading()
                view?.onScheduleCreated()
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to create schedule.")
            }
        }
    }

    fun updateSchedule(schedule: MedicationSchedule) {
        view?.showLoading()
        presenterScope.launch {
            try {
                scheduleRepository.updateSchedule(schedule)
                if (schedule.isRecurring) {
                    reminderService.setReminder(schedule)
                }
                view?.hideLoading()
                view?.onScheduleUpdated()
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to update schedule.")
            }
        }
    }

    fun deleteSchedule(scheduleId: String) {
        view?.showLoading()
        presenterScope.launch {
            try {
                scheduleRepository.deleteSchedule(scheduleId)
                reminderService.cancelReminder(scheduleId)
                view?.hideLoading()
                view?.onScheduleDeleted()
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to delete schedule.")
            }
        }
    }

    fun markDoseTaken(scheduleId: String, timestamp: Long) {
        presenterScope.launch {
            try {
                scheduleRepository.markDoseTaken(scheduleId, timestamp)
            } catch (e: Exception) {
                view?.displayError(e.message ?: "Failed to log dose.")
            }
        }
    }

    fun markDoseMissed(scheduleId: String) {
        presenterScope.launch {
            try {
                scheduleRepository.markDoseMissed(scheduleId)
            } catch (e: Exception) {
                view?.displayError(e.message ?: "Failed to log missed dose.")
            }
        }
    }

    fun loadMissedDoses() {
        view?.showLoading()
        presenterScope.launch {
            scheduleRepository.loadMissedDoses().collectLatest {
                view?.displayMissedDoses(it)
                view?.hideLoading()
            }
        }
    }

    fun updateNotificationSettings(settings: NotificationSettings) {
        presenterScope.launch {
            try {
                scheduleRepository.updateNotificationSettings(settings)
            } catch (e: Exception) {
                view?.displayError(e.message ?: "Failed to update settings.")
            }
        }
    }
}
