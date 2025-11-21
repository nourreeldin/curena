
package com.fueians.medicationapp.presenter.Login

import com.fueians.medicationapp.model.services.AuthResponse


// DELETE AFTER FINISHING
// DELETE AFTER FINISHING
// DELETE AFTER FINISHING
// DELETE AFTER FINISHING
class LoginView {
    fun showEmailEmpty() {}
    fun showNotValidEmail() {}
    fun showEmptyPassword() {}
    fun LoginFailed() {}
    fun emailNotExist() {}
    fun Success(Information: List<String>) {}
}

class AuthRepository {
    fun login(email: String, password: String): List<String> {
        val errors = mutableListOf<String>()
        return errors
    }

    fun emailExist(email : String): Boolean{
        return true
    }
}

// DELETE AFTER FINISHING
// DELETE AFTER FINISHING
// DELETE AFTER FINISHING
// DELETE AFTER FINISHING

class LoginPresenter (private val View : LoginView) {
    private val Communicator = AuthRepository()

    fun Login(email: String, password: String) {
        // Step 1
// Step 1
        if (email.isEmpty()){
            View.showEmailEmpty() // email is empty
        }

        if (!checkEmailSyntax(email)){
            View.showNotValidEmail() // email not written correctly
        }

        if(!checkEmail(email)){
            View.emailNotExist() // email doesnt exist in Database , signup ?
        }

        //Step 2
        if (password.isEmpty()){
            View.showEmptyPassword() // password is empty
        }

        // Step 3
        val Information = Communicator.login(email,password)
        if(Information == null ){
            View.LoginFailed() // Incorrect Password since email already exists.
        }
        else{
            View.Success(Information) // return either userentity or its information
        }


    }

    private fun checkEmail(email: String): Boolean {
        return (Communicator.emailExist(email))
    }
    private fun checkEmailSyntax(email: String) : Boolean {
        val emailRegex = Regex(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            RegexOption.IGNORE_CASE
        )
        return emailRegex.matches(email)
    }

}

