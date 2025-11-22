package com.fueians.medicationapp.presenter.Login

import com.fueians.medicationapp.model.entities.UserEntity
import com.fueians.medicationapp.model.repository.UserRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

// =========================================================================
// 1. View Interface (no changes needed)
// =========================================================================

interface LoginView {
    fun showLoginError(message: String)
    fun showLoading()
    fun hideLoading()
    fun onLoginSuccess(user: UserEntity)
}

// =========================================================================
// 2. Presenter (Updated for RxJava)
// =========================================================================

class LoginPresenter(private var view: LoginView?) {

    private val userRepository = UserRepository()
    private val compositeDisposable = CompositeDisposable()

    fun login(email: String, password: String) {
        if (email.isBlank()) {
            view?.showLoginError("Email is required")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view?.showLoginError("Invalid email format")
            return
        }
        if (password.isBlank()) {
            view?.showLoginError("Password is required")
            return
        }

        view?.showLoading()

        val disposable = userRepository.login(email, password)
            .observeOn(AndroidSchedulers.mainThread()) // Ensure UI updates are on the main thread
            .subscribe({
                // onSuccess
                view?.hideLoading()
                view?.onLoginSuccess(it)
            }, {
                // onError
                view?.hideLoading()
                view?.showLoginError(it.message ?: "Login failed")
            })

        compositeDisposable.add(disposable)
    }

    fun detachView() {
        view = null
        compositeDisposable.clear() // Dispose all subscriptions
    }
}
