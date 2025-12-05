package com.fueians.medicationapp.presenter.EmailVerification

import com.fueians.medicationapp.view.interfaces.IEmailVerificationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EmailVerificationPresenter(
    private var view: IEmailVerificationView?,
) {

    private val presenterScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // In-memory temporary storage (example simulation)
    private var generatedCode: String? = null


    fun attachView(view: IEmailVerificationView) {
        this.view = view
    }

    fun detachView() {
        view = null
        presenterScope.cancel()
    }


    fun sendVerificationEmail(email: String) {
        presenterScope.launch {

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                view?.showVerificationError("Invalid email format.")
                return@launch
            }

            view?.showLoading()

            try {
                // Simulate sending email and generating a code
                generatedCode = generateVerificationCode()

                delay(1500)  // simulate network delay (optional)

                view?.onEmailSentSuccess()

            } catch (e: Exception) {
                view?.showErrorMessage("Failed to send verification email: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }

    fun resendCode(email: String) {
        sendVerificationEmail(email)
    }


    fun verifyCode(email: String, code: String) {
        presenterScope.launch {

            if (code.isBlank()) {
                view?.showVerificationError("Verification code is required.")
                return@launch
            }

            view?.showLoading()

            try {
                // Compare user input with generated code
                if (code == generatedCode) {
                    view?.onVerificationSuccess("USER_ID_123")  // example user ID
                } else {
                    view?.showVerificationError("Incorrect verification code.")
                }

            } catch (e: Exception) {
                view?.showErrorMessage("An error occurred: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }

    private fun generateVerificationCode(): String {
        return (100000..999999).random().toString()
    }
}
