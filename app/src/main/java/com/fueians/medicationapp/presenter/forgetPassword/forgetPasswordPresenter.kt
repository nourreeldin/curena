package com.fueians.medicationapp.presenter.forgetPassword

import com.fueians.medicationapp.model.repository.AuthRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

interface ForgetView {
    fun showProgress()
    fun hideProgress()
    fun setSuccess()
    fun setFailure(message: String)
}

class ForgetPasswordPresenter(private var view: ForgetView?) {

    private val authRepository = AuthRepository()
    private val compositeDisposable = CompositeDisposable()

    fun sendPasswordResetEmail(email: String) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view?.setFailure("Invalid email format")
            return
        }

        view?.showProgress()
        val disposable = authRepository.sendPasswordResetEmail(email)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideProgress()
                view?.setSuccess()
            }, {
                view?.hideProgress()
                view?.setFailure(it.message ?: "Failed to send reset email.")
            })
        compositeDisposable.add(disposable)
    }

    fun detachView() {
        view = null
        compositeDisposable.clear()
    }
}
