
package com.fueians.medicationapp.presenter.Login

// ############################################################
// ######################### Temporary ########################
// ####### UserRepository (Model not committed yet) ###########
// ####### LoginView (View not implemented yet) ###############
// ############################################################

interface LoginView {
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

interface LoginContract {

    // Entry point
    fun login(email: String, password: String) {}
    fun SignUpBtn() {}
    fun loginWithGoogle(idToken: String) {}
    fun loginWithFacebook(accessToken: String) {}

    // Steps
    private fun checkEmail(email: String) {}
    private fun checkPassword(email: String, password: String) {}
    private fun changeViewToDashboard() {}

    // Cases
    private fun onEmailWrongSyntax() {}
    private fun onEmailNotFound() {}
    private fun onEmailEmpty() {}
    private fun onEmailCorrect() {}

    private fun onPasswordWrong() {}
    private fun onPasswordEmpty() {}
    private fun onPasswordCorrect() {}

    private fun onContinueWithGoogle() {}
    private fun onContinueWithFacebook() {}

    private fun onAccountLocked() {}
    private fun onDeviceBlocked() {}

    // ===================================================
    // ===============  Not Agreed Upon  =================
    // ===================================================
    // Helper
    private fun decreaseTries() {}
    private fun popup(message: String) {}
    private fun changeView(screen: String) {}
    // ===================================================

}

class LoginPresenter (    private val userRepository: UserRepository,
                          private val view: LoginView
) : LoginContract {
    override fun login(email: String, password: String) {


    }




}