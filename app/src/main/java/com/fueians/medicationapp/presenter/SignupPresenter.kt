package com.fueians.medicationapp.presenter
import com.fueians.medicationapp.view.interfaces.ISignupView

class SignupPresenter(
    private val view: ISignupView
) {

    fun register(fullName: String, email: String, password: String, confirmPassword: String) {

        view.showLoading()

        // Validation
        if (fullName.isBlank()) {
            view.hideLoading()
            view.showError("Full name is required")
            return
        }

        if (email.isBlank()) {
            view.hideLoading()
            view.showError("Email is required")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view.hideLoading()
            view.showError("Invalid email format")
            return
        }

        if (password.length < 6) {
            view.hideLoading()
            view.showError("Password must be at least 6 characters")
            return
        }

        if (password != confirmPassword) {
            view.hideLoading()
            view.showError("Passwords do not match")
            return
        }

        // TODO: Call repo to save to DB
        // userRepository.register(...)

        view.hideLoading()
        view.showRegistrationSuccess()
    }
}
