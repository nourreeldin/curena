package com.fueians.medicationapp.presenter

import android.content.Context
import com.fueians.medicationapp.model.repository.UserRepository
import com.fueians.medicationapp.view.interfaces.ILoginView
import com.fueians.medicationapp.model.repository.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginPresenter(
    private var view: ILoginView?,
    context: Context
) {

    private val userRepository = UserRepository(context)

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

        CoroutineScope(Dispatchers.IO).launch {

            val result = userRepository.login(email, password)

            withContext(Dispatchers.Main) {
                val v = view ?: return@withContext
                v.hideLoading()

                when (result) {
                    is Result.Success -> v.showLoginSuccess(result.data)
                    is Result.Failure -> v.showLoginError(
                        result.exception.message ?: "Login failed"
                    )
                }
            }
        }
    }

    fun detachView() {
        view = null
    }
}

