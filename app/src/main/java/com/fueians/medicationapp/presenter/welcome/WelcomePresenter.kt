package com.fueians.medicationapp.presenter.welcome

import com.fueians.medicationapp.model.repository.AuthRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

interface WelcomeView {
    fun navigateToMainApp()
    fun navigateToLogin()
}

class WelcomePresenter(private var view: WelcomeView?) {

    private val authRepository = AuthRepository()
    private val compositeDisposable = CompositeDisposable()

    fun checkAuthState() {
        val disposable = authRepository.getCurrentUser()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                // onSuccess (user exists)
                view?.navigateToMainApp()
            }, {
                // onError
                view?.navigateToLogin() // Treat error as not logged in
            }, {
                // onComplete (no user)
                view?.navigateToLogin()
            })
        compositeDisposable.add(disposable)
    }

    fun detachView() {
        view = null
        compositeDisposable.clear()
    }
}
