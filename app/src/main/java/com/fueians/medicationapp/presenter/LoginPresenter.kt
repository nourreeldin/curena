//package com.fueians.medicationapp.presenter
//
////import com.fueians.medicationapp.model.repository.UserRepository
//import com.fueians.medicationapp.view.interfaces.ILoginView
////import com.fueians.medicationapp.model.repository.Result
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//
//class LoginPresenter(private var view: ILoginView?) {
//
//    //private val userRepository: UserRepository? = UserRepository()
//
//    fun login(email: String, password: String) {
//        if (email.isBlank()) {
//            view?.showLoginError("Email is required")
//            return
//        }
//
//        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            view?.showLoginError("Invalid email format")
//            return
//        }
//
//        if (password.isBlank()) {
//            view?.showLoginError("Password is required")
//            return
//        }
//
//        view?.showLoading()
//
//        // --- Repository requires suspend ---
//        CoroutineScope(Dispatchers.IO).launch {
//            when (val result = userRepository.login(email, password)) {
//
//                is Result.Success -> {
//                    launch(Dispatchers.Main) {
//                        view?.hideLoading()
//                        view?.showLoginSuccess(result.data)
//                    }
//                }
//
//                is Result.Failure -> {
//                    launch(Dispatchers.Main) {
//                        view?.hideLoading()
//                        view?.showLoginError(
//                            result.exception.message ?: "Login failed"
//                        )
//                    }
//                }
//            }
//        }
//    }
//
//    fun detachView() {
//        view = null
//    }
//}
