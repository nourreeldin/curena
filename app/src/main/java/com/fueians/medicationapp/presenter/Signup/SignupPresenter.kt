
package com.fueians.medicationapp.presenter.Signup


// DELETE AFTER FINISHING
// DELETE AFTER FINISHING
// DELETE AFTER FINISHING
// DELETE AFTER FINISHING
class SignupView {
    fun showEmailEmpty() {}
    fun showNotValidEmail() {}
    fun showEmptyPassword() {}
    fun weakPassword() {}
    fun showConfirmPasswordEmpty() {}
    fun confirmPasswrdNotPassword() {}
    fun systemError() {}
    fun emailUsed() {}
    fun Success(Information: List<String>) {}
}

class AuthRepository {
    fun CreateAccount(name: String, email: String, password: String, confirmPassword: String): List<String> {
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


class SignupPresenter(private val View : SignupView){
    private val Communicator = AuthRepository()

    fun Signup(name: String, email: String, password: String, confirmPassword: String) {
        // Step 1
        if (email.isEmpty()){
            View.showEmailEmpty() // email is empty
        }

        if (!checkEmailSyntax(email)){
            View.showNotValidEmail() // email not written correctly
        }

        if(!checkEmail(email)){
            View.emailUsed() // email already exists in database
        }

        //Step 2
        if (password.isEmpty()){
            View.showEmptyPassword() // password is empty
        }

        if (!isStrongPassword(password)){
            View.weakPassword() // password criteria not met
        }

        if (confirmPassword.isEmpty()){
            View.showConfirmPasswordEmpty() // confirm password is empty
        }

        if (!onConfirmPasswordMatch(confirmPassword,password)){
            View.confirmPasswrdNotPassword() // confirm password is not like main password
        }

        val Information = Communicator.CreateAccount(name,email,password,confirmPassword)
        if(Information == null ){
            View.systemError() // cant signup ( system error )
        }
        else{
            View.Success(Information) // successful signup , give view userentity or its information
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

    private fun isStrongPassword(password: String): Boolean {

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

}


