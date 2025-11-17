
package com.fueians.medicationapp.presenter.Signup

import com.fueians.medicationapp.model.services.AuthResponse
import com.fueians.medicationapp.presenter.Login.AuthRepository

// ############################################################
// ######################### Temporary ########################
// ####### UserRepository (Waiting For Confirmation) ##########
// ####### LoginView (View not implemented yet) ###############
// ############################################################

class SignupView {
    fun showPopup(message: String) {}
    fun changeView(screen: String) {}
}

class AuthRepository {
    suspend fun signup(email: String, password: String): AuthResponse? {
        return null
    }

    fun addUser(name: String, email: String, password: String){}
}

data class UserEntity(val name: String, val email: String)

// ############################################################
// ############################################################
// ############################################################

interface SignupContract {

    // Entry Point
    fun Signup(name: String, email: String, password: String, confirmPassword: String) {}
    fun onLoginBtnClicked() {}
    fun signupWithGoogle(idToken: String) {}
    fun signupWithFacebook(accessToken: String) {}

//    // Steps
//    private fun checkEmail(email: String) {}
//    private fun isStrongPassword(password: String) {}
//    private fun checkConfirmPassword(password: String, confirmPassword: String) {}
//    private fun createAccount(name: String, email: String, password: String) {}
//
//    // Cases
//    private fun changeViewToLogin() {}
//    private fun signup(name: String, email: String, password: String, confirmPassword: String) {}
//    private fun signupWithGoogle(idToken: String) {}
//    private fun signupWithFacebook(accessToken: String) {}
//
//    // Cases
//    private fun onEmailEmpty() {}
//    private fun onEmailWrongSyntax() {}
//    private fun onEmailAlreadyExists() {}
//    private fun onPasswordEmpty() {}
//    private fun onPasswordWeak() {}
//    private fun onConfirmPasswordEmpty() {}
//    private fun onConfirmPasswordMismatch() {}
//    private fun onContinueWithGoogle() {}
//    private fun onContinueWithFacebook() {}
//    private fun onDeviceBlocked() {}
//
//    // ===================================================
//    // ================= Helpers =========================
//    // ===================================================
//    private fun popup(message: String) {}
//    private fun changeView(screen: String) {}
//    // ===================================================

}

class SignupPresenter : SignupContract{
    private val Communicator = AuthRepository()
    private val View = SignupView()

    override fun Signup(name: String, email: String, password: String, confirmPassword: String) {
        // Step 1
        if (email.isEmpty()){
            onEmailEmpty()
            return
        }

        if (!checkEmailSyntax(email)){
            onEmailWrongSyntax()
            return
        }

        //        checkEmail(email)

        //Step 2
        if (password.isEmpty()){
            onPasswordEmpty()
            return
        }

        if (!isStrongPassword(password)){
            onPasswordWeak()
            return
        }

        if (confirmPassword.isEmpty()){
            onConfirmPasswordEmpty()
            return
        }

        if (!onConfirmPasswordMatch(confirmPassword,password)){
            onConfirmPasswordMismatch()
            return
        }

        createAccount(email,password)

    }

    // ============================================================
    // =============== Not Implemented In Repo ====================
    //    private fun checkEmail(email: String) {
    //
    //    }
    //    private fun onEmailNotFound() {
    //
    //    }
    // =============== Not Implemented In Repo ====================
    // ============================================================

    override fun onLoginBtnClicked() {
        changeView("null")
    }


    private fun checkEmailSyntax(email: String) : Boolean {
        val emailRegex = Regex(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            RegexOption.IGNORE_CASE
        )
        return emailRegex.matches(email)
    }

    fun isStrongPassword(password: String): Boolean {

        if (password.length < 8)
            return false

        if (!password.contains(Regex("[A-Z]")))
            return false

        if (!password.contains(Regex("[a-z]")))
            return false

        if (!password.contains(Regex("\\d")))
            return false

        if (!password.contains(Regex("[@\$!%*?&]")))
            return false

        return true
    }

    private fun onConfirmPasswordMatch(confirmPassword: String, password: String): Boolean {
        return confirmPassword == password
    }

    private fun createAccount(name: String, email: String, password: String) {

    }
    private fun createAccount( email: String, password: String) {

    }

    private fun signup(name: String, email: String, password: String, confirmPassword: String) {

    }

    private fun checkEmail(email: String) {

    }

    private fun checkConfirmPassword(password: String, confirmPassword: String) {

    }
    private fun changeViewToLogin() {

    }

    // ===================================================
    // ================= Cases ==========================
    // ===================================================
    private fun onEmailEmpty() {

    }
    private fun onEmailWrongSyntax() {

    }
    private fun onEmailAlreadyExists() {

    }
    private fun onEmailValid() {

    }

    private fun onPasswordEmpty() {

    }
    private fun onPasswordWeak() {

    }
    private fun onPasswordValid() {

    }

    private fun onConfirmPasswordEmpty() {

    }
    private fun onConfirmPasswordMismatch() {

    }

    private fun onDeviceBlocked() {

    }

    // ===================================================
    // ================= Helpers ========================
    // ===================================================
    private fun popup(message: String) {}
    private fun changeView(screen: String) {}



    // ===================================================
    // ================== Not Implemented Yet ============

    override fun signupWithFacebook(accessToken: String) {
        onContinueWithFacebook()
    }

    override fun signupWithGoogle(idToken: String) {
        onContinueWithGoogle()
    }

    private fun onContinueWithGoogle() {

    }

    private fun onContinueWithFacebook() {

    }

    // ================== Not Implemented Yet ============
    // ===================================================

}


