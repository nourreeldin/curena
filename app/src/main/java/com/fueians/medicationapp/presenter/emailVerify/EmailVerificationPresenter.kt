package com.fueians.medicationapp.presenter.emailVerify

import com.fueians.medicationapp.model.repository.AuthRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

interface EmailVerificationView {
    fun showLoading()
    fun hideLoading()
    fun onVerificationStatus(isVerified: Boolean)
    fun onVerificationEmailSent()
    fun showError(message: String)
}

class EmailVerificationPresenter(private var view: EmailVerificationView?) {

    private val authRepository = AuthRepository()
    private val compositeDisposable = CompositeDisposable()

    fun checkVerificationStatus() {
        view?.showLoading()
        val disposable = authRepository.checkEmailVerificationStatus()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.onVerificationStatus(it)
            }, {
                view?.hideLoading()
                view?.showError(it.message ?: "Failed to check status.")
            })
        compositeDisposable.add(disposable)
    }

    fun resendVerificationEmail() {
        view?.showLoading()
        val disposable = authRepository.resendVerificationEmail()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.onVerificationEmailSent()
            }, {
                view?.hideLoading()
                view?.showError(it.message ?: "Failed to resend email.")
            })
        compositeDisposable.add(disposable)
    }

    fun detachView() {
        view = null
        compositeDisposable.clear()
    }
}
