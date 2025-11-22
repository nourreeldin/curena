package com.fueians.medicationapp.presenter.Signup

import com.fueians.medicationapp.model.entities.UserEntity
import com.fueians.medicationapp.model.repository.UserRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

// =========================================================================
// 1. View Interface (no changes needed)
// =========================================================================

interface SignupView {
    fun showSignupError(message: String)
    fun showLoading()
    fun hideLoading()
    fun onSignupSuccess(user: UserEntity)
}

// =========================================================================
// 2. Presenter (Updated for RxJava)
// =========================================================================

class SignupPresenter(private var view: SignupView?) {

    private val userRepository = UserRepository()
    private val compositeDisposable = CompositeDisposable()

    fun signup(name: String, email: String, password: String, confirmPassword: String) {
        if (name.isBlank()) {
            view?.showSignupError("Full name is required")
            return
        }
        if (email.isBlank()) {
            view?.showSignupError("Email is required")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view?.showSignupError("Invalid email format")
            return
        }
        if (password.isBlank()) {
            view?.showSignupError("Password is required")
            return
        }
        if (!isStrongPassword(password)) {
            view?.showSignupError("Password must be 8+ characters and include uppercase, lowercase, a digit, and a special character.")
            return
        }
        if (password != confirmPassword) {
            view?.showSignupError("Passwords do not match")
            return
        }

        view?.showLoading()

        val disposable = userRepository.createAccount(name, email, password)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.hideLoading()
                view?.onSignupSuccess(it)
            }, {
                view?.hideLoading()
                view?.showSignupError(it.message ?: "Signup failed due to an unknown error.")
            })

        compositeDisposable.add(disposable)
    }

    private fun isStrongPassword(password: String): Boolean {
        return password.length >= 8 &&
               password.contains(Regex("[A-Z]")) &&
               password.contains(Regex("[a-z]")) &&
               password.contains(Regex("\\d")) &&
               password.contains(Regex("[@$!%*?&]"))
    }

    fun detachView() {
        view = null
        compositeDisposable.clear()
    }
}
