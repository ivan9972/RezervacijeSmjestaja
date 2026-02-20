package ba.sum.fsre.loginfirebase.repository

import ba.sum.fsre.loginfirebase.auth.FirebaseAuthManager
import ba.sum.fsre.loginfirebase.dao.KorisnikDao
import ba.sum.fsre.loginfirebase.entity.Korisnik

class AuthRepository(
    private val authManager: FirebaseAuthManager,
    private val korisnikDao: KorisnikDao
) {

    fun login(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        authManager.login(email, password, onResult)
    }

    fun register(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        authManager.register(email, password, onResult)
    }

    suspend fun saveUserToRoom(firebaseUid: String, ime: String, prezime: String) {
        val korisnik = Korisnik(
            firebaseUid = firebaseUid,
            ime = ime,
            prezime = prezime
        )
        korisnikDao.insert(korisnik)
    }
}