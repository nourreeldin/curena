package com.fueians.medicationapp.presenter.Schedule

import com.fueians.medicationapp.model.repository.ScheduleRepository
import com.fueians.medicationapp.model.entities.MedicationSchedule
import com.fueians.medicationapp.presenter.TestRepo.NotificationSettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

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

    private val scheduleRepository = ScheduleRepository()
    private val reminderService = ReminderService()
    private val compositeDisposable = CompositeDisposable()

    fun attachView(view: IScheduleView) {
        this.view = view
    }

    fun detachView() {
        view = null
        compositeDisposable.clear()
    }

    fun loadSchedules() {
        view?.showLoading()
        val disposable = scheduleRepository.loadSchedules()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.displaySchedules(it)
            }, {
                view?.hideLoading()
                view?.displayError(it.message ?: "Failed to load schedules.")
            })
        compositeDisposable.add(disposable)
    }

    fun loadTodaySchedules() {
        view?.showLoading()
        val disposable = scheduleRepository.loadTodaySchedules()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.displaySchedules(it)
            }, {
                view?.hideLoading()
                view?.displayError(it.message ?: "Failed to load today's schedules.")
            })
        compositeDisposable.add(disposable)
    }

    fun createSchedule(schedule: MedicationSchedule) {
        view?.showLoading()
        val disposable = scheduleRepository.createSchedule(schedule)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (schedule.isRecurring) {
                    reminderService.setReminder(schedule)
                }
                view?.hideLoading()
                view?.onScheduleCreated()
            }, {
                view?.hideLoading()
                view?.displayError(it.message ?: "Failed to create schedule.")
            })
        compositeDisposable.add(disposable)
    }

    fun updateSchedule(schedule: MedicationSchedule) {
        view?.showLoading()
        val disposable = scheduleRepository.updateSchedule(schedule)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (schedule.isRecurring) {
                    reminderService.setReminder(schedule)
                }
                view?.hideLoading()
                view?.onScheduleUpdated()
            }, {
                view?.hideLoading()
                view?.displayError(it.message ?: "Failed to update schedule.")
            })
        compositeDisposable.add(disposable)
    }

    fun deleteSchedule(scheduleId: String) {
        view?.showLoading()
        val disposable = scheduleRepository.deleteSchedule(scheduleId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                reminderService.cancelReminder(scheduleId)
                view?.hideLoading()
                view?.onScheduleDeleted()
            }, {
                view?.hideLoading()
                view?.displayError(it.message ?: "Failed to delete schedule.")
            })
        compositeDisposable.add(disposable)
    }

    fun markDoseTaken(scheduleId: String, timestamp: Long) {
        val disposable = scheduleRepository.markDoseTaken(scheduleId, timestamp)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {
                view?.displayError(it.message ?: "Failed to log dose.")
            })
        compositeDisposable.add(disposable)
    }

    fun markDoseMissed(scheduleId: String) {
        val disposable = scheduleRepository.markDoseMissed(scheduleId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {
                view?.displayError(it.message ?: "Failed to log missed dose.")
            })
        compositeDisposable.add(disposable)
    }

    fun loadMissedDoses() {
        view?.showLoading()
        val disposable = scheduleRepository.loadMissedDoses()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.displayMissedDoses(it)
            }, {
                view?.hideLoading()
                view?.displayError(it.message ?: "Failed to load missed doses.")
            })
        compositeDisposable.add(disposable)
    }

    fun updateNotificationSettings(settings: NotificationSettings) {
        val disposable = scheduleRepository.updateNotificationSettings(settings)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {
                view?.displayError(it.message ?: "Failed to update settings.")
            })
        compositeDisposable.add(disposable)
    }
}
