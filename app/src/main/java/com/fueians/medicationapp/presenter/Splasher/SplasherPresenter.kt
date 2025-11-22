package com.fueians.medicationapp.presenter.Splasher

import com.fueians.medicationapp.model.repository.AuthRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

interface SplasherView {
    fun navigateToMainApp()
    fun navigateToLogin()
}

class SplasherPresenter(private var view: SplasherView?) {

    private val authRepository = AuthRepository()
    private val compositeDisposable = CompositeDisposable()
    private val minSplashTimeMs = 2000L // 2 seconds

    /**
     * Starts the splash screen logic: waits for a minimum time, checks auth state,
     * and then navigates to the appropriate screen.
     */
    fun start() {
        val disposable = Single.timer(minSplashTimeMs, TimeUnit.MILLISECONDS)
            .flatMapMaybe { authRepository.getCurrentUser() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                // onSuccess (user is logged in)
                view?.navigateToMainApp()
            }, {
                // onError
                view?.navigateToLogin() // On any error, go to login
            }, {
                // onComplete (no user is logged in)
                view?.navigateToLogin()
            })

        compositeDisposable.add(disposable)
    }

    fun detachView() {
        view = null
        compositeDisposable.clear()
    }
}
