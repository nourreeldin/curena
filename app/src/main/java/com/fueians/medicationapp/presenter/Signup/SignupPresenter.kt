
package com.fueians.medicationapp.presenter.Signup

// ############################################################
// ######################### Temporary ########################
// ####### UserRepository (Model not committed yet) ###########
// ####### LoginView (View not implemented yet) ###############
// ############################################################

interface SignupView {
    fun showPopup(message: String) {}
    fun changeView(screen: String) {}
}

interface UserRepository {
    fun findUserByEmail(email: String): User?
    fun addUser(name: String, email: String, password: String)
}

data class User(
    val name: String,
    val email: String,
    val password: String
)

// ############################################################
// ############################################################
// ############################################################

interface SignupContract {

    // ===================================================
    // ================= Public Button Functions ========
    // ===================================================
    fun onSignUpBtnClicked(name: String, email: String, password: String, confirmPassword: String) {}
    fun onContinueWithGoogleClicked(idToken: String?) {}
    fun onContinueWithFacebookClicked(accessToken: String?) {}
    fun onLoginBtnClicked() {}

    // ===================================================
    // ================= Signup Logic ===================
    // ===================================================
    private fun signup(name: String, email: String, password: String, confirmPassword: String) {}
    private fun signupWithGoogle(idToken: String) {}
    private fun signupWithFacebook(accessToken: String) {}

    // ===================================================
    // ================= Steps ==========================
    // ===================================================
    private fun checkEmail(email: String) {}
    private fun checkPassword(password: String) {}
    private fun checkConfirmPassword(password: String, confirmPassword: String) {}
    private fun createAccount(name: String, email: String, password: String) {}
    private fun changeViewToLogin() {}

    // ===================================================
    // ================= Cases ==========================
    // ===================================================
    private fun onEmailEmpty() {}
    private fun onEmailWrongSyntax() {}
    private fun onEmailAlreadyExists() {}
    private fun onEmailValid() {}

    private fun onPasswordEmpty() {}
    private fun onPasswordWeak() {}
    private fun onPasswordValid() {}

    private fun onConfirmPasswordEmpty() {}
    private fun onConfirmPasswordMismatch() {}
    private fun onConfirmPasswordMatch() {}

    private fun onDeviceBlocked() {}

    // ===================================================
    // ================= Helpers ========================
    // ===================================================
    private fun decreaseTries() {}
    private fun popup(message: String) {}
    private fun changeView(screen: String) {}

}

class SignupPresenter(
    private val view: SignupView,
    private val userRepository: UserRepository
) {



}


