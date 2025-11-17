
package com.fueians.medicationapp.presenter.Login

import com.fueians.medicationapp.model.services.AuthResponse

// ############################################################
// ######################### Temporary ########################
// ####### UserRepository (Model not committed yet) ###########
// ####### LoginView (View not implemented yet) ###############
// ############################################################

class LoginView {
    fun showPopup(message: String) {}
    fun changeView(screen: String) {}
}

class AuthRepository {
    suspend fun login(email: String, password: String): AuthResponse? {
        return null
    }

    fun addUser(name: String, email: String, password: String){}
}

data class UserEntity(val name: String, val email: String)

// ############################################################
// ############################################################
// ############################################################

interface LoginContract {

    // Entry Point
    fun Login(email: String, password: String) {}
    fun signupBtn() {}
    fun loginWithGoogle(idToken: String) {}
    fun loginWithFacebook(accessToken: String) {}

//    // Steps
//    private fun checkEmail(email: String) {}
//    private fun checkPassword(email: String, password: String) {}
//    private fun changeViewToDashboard() {}
//
//    // Cases
//    private fun onEmailEmpty() {}
//    private fun checkEmailSyntax(email: String):Boolean {}
//    private fun onEmailWrongSyntax() {}
//    private fun onEmailNotFound() {}
//    private fun onPasswordWrong() {}
//    private fun onPasswordEmpty() {}
//    private fun onContinueWithGoogle() {}
//    private fun onContinueWithFacebook() {}
//    private fun onAccountLocked() {}
//    private fun onDeviceBlocked() {}
//
//    // ===================================================
//    // ===============  Not Agreed Upon  =================
//    // ===================================================
//    private fun popup(message: String) {}
//    private fun changeView(screen: String) {}
//    // ===================================================

}

class LoginPresenter : LoginContract {
    private val Communicator = AuthRepository()
    private val View = LoginView()

    override fun Login(email: String, password: String) {
        // Step 1
        if (email.isEmpty()){
            onEmailEmpty()
            return
        }

        if (!checkEmailSyntax(email)) {
            onEmailWrongSyntax()
            return
        }

        //        checkEmail(email);

        // Step 2
        if (password.isEmpty()){
            onPasswordEmpty()
            return
        }

        if (!checkPassword(email,password)){
            onPasswordWrong()
            return
        }

        // Step 3
        onPasswordCorrect(email,password)


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

    override fun signupBtn() {
        changeView("null");
    }


    private fun checkEmailSyntax(email: String) : Boolean {
        val emailRegex = Regex(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            RegexOption.IGNORE_CASE
        )
        return emailRegex.matches(email)
    }
    private fun checkPassword(email: String, password: String) : Boolean {
//        Communicator.login(email,password) // should I use suspend ????
    return true
    }

    // Cases
    private fun onEmailWrongSyntax() {
        popup("null")
    }
    private fun onEmailEmpty() {
        popup("null")
    }

    // =====================================================
    // =============== No Longer Needed ====================
    //    private fun onEmailCorrect() {
    //
    //    }
    // =============== No Longer Needed ====================
    // =====================================================


    private fun onPasswordWrong() {
        popup("null")
    }
    private fun onPasswordEmpty() {
        popup("null")
    }
    private fun onPasswordCorrect(email: String,password: String) {
        changeView("null")
    }



    // ===================================================
    // ===============  Not Agreed Upon  =================

    private fun onAccountLocked() {}
    private fun onDeviceBlocked() {}

    private fun popup(message: String) {

    }
    private fun changeView(screen: String) {

    }


    // ===============  Not Agreed Upon  =================
    // ===================================================



    // ===================================================
    // ================== Not Implemented Yet ============

    override fun loginWithFacebook(accessToken: String) {
        onContinueWithFacebook()
    }

    override fun loginWithGoogle(idToken: String) {
        onContinueWithGoogle()
    }

    private fun onContinueWithGoogle() {

    }

    private fun onContinueWithFacebook() {

    }

    // ================== Not Implemented Yet ============
    // ===================================================


}

private class test{
    val test = LoginPresenter();
}
