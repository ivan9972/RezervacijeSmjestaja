package ba.sum.fsre.loginfirebase.viewmodel

import androidx.lifecycle.ViewModel
import ba.sum.fsre.loginfirebase.repository.AuthRepository

class LoginViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    fun login(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        repository.login(email, password, onResult)
    }

    fun register(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        repository.register(email, password, onResult)
    }
}