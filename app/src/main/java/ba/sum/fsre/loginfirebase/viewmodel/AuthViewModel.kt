package ba.sum.fsre.loginfirebase.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ba.sum.fsre.loginfirebase.entity.Korisnik
import ba.sum.fsre.loginfirebase.repository.RepositoryProvider
import kotlinx.coroutines.launch

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = RepositoryProvider.auth(app.applicationContext)

    val loading = MutableLiveData(false)
    val error = MutableLiveData<String?>(null)
    val korisnik = MutableLiveData<Korisnik?>(null)

    fun login(email: String, password: String) {
        loading.value = true
        error.value = null

        viewModelScope.launch {
            val res = repo.login(email, password)
            loading.value = false

            if (res.success && res.korisnik != null) korisnik.value = res.korisnik
            else error.value = res.message ?: "Greška: korisnik nije učitan."
        }
    }

    fun register(ime: String, prezime: String, email: String, telefon: String?, password: String) {
        loading.value = true
        error.value = null

        viewModelScope.launch {
            val res = repo.register(ime, prezime, email, telefon, password)
            loading.value = false

            if (res.success && res.korisnik != null) korisnik.value = res.korisnik
            else error.value = res.message ?: "Greška: korisnik nije spremljen."
        }
    }

    fun logout() = repo.logout()
}