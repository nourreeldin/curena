package com.fueians.medicationapp.presenter.Schedule

import android.content.Context
import com.fueians.medicationapp.model.entities.MedicationScheduleEntity
import com.fueians.medicationapp.model.repository.ScheduleRepository
import com.fueians.medicationapp.view.interfaces.IScheduleView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SchedulePresenter(private var view: IScheduleView?, context: Context) {
    private val scheduleRepository = ScheduleRepository(context)
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
                scheduleRepository.getAllSchedules().let { schedules ->
                    view?.hideLoading()
                    view?.displaySchedules(schedules)
                }
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to load schedules.")
            }
        }
    }

    fun createSchedule(schedule: MedicationScheduleEntity) {
        view?.showLoading()
        presenterScope.launch {
            try {
                withContext(Dispatchers.IO) { scheduleRepository.createSchedule(schedule) }
                view?.hideLoading()
                view?.onScheduleCreated()
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to create schedule.")
            }
        }
    }

    fun updateSchedule(schedule: MedicationScheduleEntity) {
        view?.showLoading()
        presenterScope.launch {
            try {
                withContext(Dispatchers.IO) { scheduleRepository.updateSchedule(schedule) }
                view?.hideLoading()
                view?.onScheduleUpdated()
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to update schedule.")
            }
        }
    }

    fun deleteSchedule(schedule: MedicationScheduleEntity) {
        view?.showLoading()
        presenterScope.launch {
            try {
                withContext(Dispatchers.IO) { scheduleRepository.deleteSchedule(schedule) }
                view?.hideLoading()
                view?.onScheduleDeleted()
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to delete schedule.")
            }
        }
    }

    fun markDoseTaken(userId: String, medicationId: String, schedule: MedicationScheduleEntity) {
        presenterScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    scheduleRepository.markDoseTaken(userId, medicationId, schedule)
                }
            } catch (e: Exception) {
                view?.displayError(e.message ?: "Failed to log dose.")
            }
        }
    }

    fun markDoseMissed(userId: String, medicationId: String, schedule: MedicationScheduleEntity) {
        presenterScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    scheduleRepository.markDoseMissed(userId, medicationId, schedule)
                }
            } catch (e: Exception) {
                view?.displayError(e.message ?: "Failed to log missed dose.")
            }
        }
    }

    fun loadMissedDoses() {
        view?.showLoading()
        presenterScope.launch {
            try {
                val doses = withContext(Dispatchers.IO) { scheduleRepository.getMissedDoses() }
                view?.hideLoading()
                view?.displayMissedDoses(doses)
            } catch (e: Exception) {
                view?.hideLoading()
                view?.displayError(e.message ?: "Failed to load missed doses.")
            }
        }
    }
}
