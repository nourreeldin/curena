package com.fueians.medicationapp.presenter.Schedule

import com.fueians.medicationapp.model.repository.ScheduleRepository
import com.fueians.medicationapp.model.entities.MedicationSchedule
import com.fueians.medicationapp.presenter.TestRepo.NotificationSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Placeholder service - this would have its own file
class ReminderService {
    fun setReminder(schedule: MedicationSchedule) {
        println("Reminder set for schedule: ${schedule.id}")
    }

    fun cancelReminder(scheduleId: String) {
        println("Reminder canceled for schedule: $scheduleId")
    }
}

class SchedulePresenter(private var view: IScheduleView?) {

    // Dependencies are now private attributes, instantiated by the presenter.
    private val scheduleRepository = ScheduleRepository()
    private val reminderService = ReminderService()

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
            try {
                val schedules = withContext(Dispatchers.IO) {
                    scheduleRepository.loadSchedules()
                }
                view?.hideLoading()
                view?.displaySchedules(schedules)
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to load schedules.")
            }
        }
    }

    fun loadTodaySchedules() {
        view?.showLoading()
        presenterScope.launch {
            try {
                val schedules = withContext(Dispatchers.IO) {
                    scheduleRepository.loadTodaySchedules()
                }
                view?.hideLoading()
                view?.displaySchedules(schedules)
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to load today's schedules.")
            }
        }
    }

    fun createSchedule(schedule: MedicationSchedule) {
        view?.showLoading()
        presenterScope.launch {
            try {
                withContext(Dispatchers.IO) { scheduleRepository.createSchedule(schedule) }
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
                withContext(Dispatchers.IO) { scheduleRepository.updateSchedule(schedule) }
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
                withContext(Dispatchers.IO) { scheduleRepository.deleteSchedule(scheduleId) }
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
                withContext(Dispatchers.IO) { scheduleRepository.markDoseTaken(scheduleId, timestamp) }
            } catch (e: Exception) {
                view?.displayError(e.message ?: "Failed to log dose.")
            }
        }
    }

    fun markDoseMissed(scheduleId: String) {
        presenterScope.launch {
            try {
                withContext(Dispatchers.IO) { scheduleRepository.markDoseMissed(scheduleId) }
            } catch (e: Exception) {
                view?.displayError(e.message ?: "Failed to log missed dose.")
            }
        }
    }

    fun loadMissedDoses() {
        view?.showLoading()
        presenterScope.launch {
            try {
                val doses = withContext(Dispatchers.IO) { scheduleRepository.loadMissedDoses() }
                view?.hideLoading()
                view?.displayMissedDoses(doses)
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to load missed doses.")
            }
        }
    }

    fun updateNotificationSettings(settings: NotificationSettings) {
        presenterScope.launch {
            try {
                withContext(Dispatchers.IO) { scheduleRepository.updateNotificationSettings(settings) }
            } catch (e: Exception) {
                view?.displayError(e.message ?: "Failed to update settings.")
            }
        }
    }
}
