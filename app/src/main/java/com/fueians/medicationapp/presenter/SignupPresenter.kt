package com.fueians.medicationapp.presenter

import android.content.Context
import com.fueians.medicationapp.model.repository.Result
import com.fueians.medicationapp.model.repository.UserRepository
import com.fueians.medicationapp.view.interfaces.ISignupView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupPresenter(
    private var view: ISignupView?,
    context: Context
) {

    private val userRepository = UserRepository(context)

    fun register(fullName: String, email: String, password: String, confirmPassword: String) {

        // Loading UI state
        view?.showLoading()

        // ---- VALIDATION ----
        if (fullName.isBlank()) {
            view?.hideLoading()
            view?.showError("Full name is required")
            return
        }

        if (email.isBlank()) {
            view?.hideLoading()
            view?.showError("Email is required")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view?.hideLoading()
            view?.showError("Invalid email format")
            return
        }

        if (password.length < 6) {
            view?.hideLoading()
            view?.showError("Password must be at least 6 characters")
            return
        }

        if (password != confirmPassword) {
            view?.hideLoading()
            view?.showError("Passwords do not match")
            return
        }


        CoroutineScope(Dispatchers.IO).launch {

            val result = userRepository.register(
                email = email,
                password = password,
                name = fullName
            )

            withContext(Dispatchers.Main) {

                val v = view ?: return@withContext
                v.hideLoading()

                when (result) {
                    is Result.Success -> {
                        v.showRegistrationSuccess()
                    }

                    is Result.Failure -> {
                        v.showError(result.exception.message ?: "Registration failed")
                    }
                }
            }
        }
    }

    fun detachView() {
        view = null
    }
}
