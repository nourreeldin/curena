package com.fueians.medicationapp.presenter.User

import com.fueians.medicationapp.model.entities.UserEntity
import com.fueians.medicationapp.model.repository.UserRepository
import com.fueians.medicationapp.presenter.Iprofile.IProfileView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

// =========================================================================
// 1. Placeholder Service (no changes needed)
// =========================================================================

class SecurityManager {
    fun logout() {
        println("User logged out.")
    }
}

// =========================================================================
// 2. Presenter (Updated for RxJava)
// =========================================================================

class UserPresenter(private var view: IProfileView?) {

    private val userRepository = UserRepository()
    private val securityManager = SecurityManager()
    private val compositeDisposable = CompositeDisposable()

    fun attachView(view: IProfileView) {
        this.view = view
    }

    fun detachView() {
        view = null
        compositeDisposable.clear()
    }

    fun loadUserProfile(userId: String) {
        view?.showLoading()
        val disposable = userRepository.getUserById(userId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.displayUserProfile(it)
            }, {
                view?.hideLoading()
                view?.displayError(it.message ?: "Failed to load profile.")
            })
        compositeDisposable.add(disposable)
    }

    fun updateUserProfile(user: UserEntity) {
        view?.showLoading()
        val disposable = userRepository.updateUser(user)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.onProfileUpdated()
            }, {
                view?.hideLoading()
                view?.displayError(it.message ?: "Failed to update profile.")
            })
        compositeDisposable.add(disposable)
    }

    fun changePassword(userId: String, oldPassword: String, newPassword: String) {
        view?.showLoading()
        val disposable = userRepository.changePassword(userId, oldPassword, newPassword)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.onPasswordChanged()
            }, {
                view?.hideLoading()
                view?.displayError(it.message ?: "Failed to change password.")
            })
        compositeDisposable.add(disposable)
    }

    fun updateProfilePhoto(userId: String, photoUri: String) {
        view?.showLoading()
        val disposable = userRepository.getUserById(userId).firstOrError()
            .flatMapCompletable { user ->
                userRepository.updateUser(user.copy(photoUrl = photoUri))
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.onProfileUpdated()
            }, {
                view?.hideLoading()
                view?.displayError(it.message ?: "Failed to update photo.")
            })
        compositeDisposable.add(disposable)
    }

    fun logout() {
        securityManager.logout()
        view?.onLogout()
    }
}
