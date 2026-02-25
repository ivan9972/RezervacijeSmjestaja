package ba.sum.fsre.loginfirebase.repository

import ba.sum.fsre.loginfirebase.auth.FirebaseAuthManager
import ba.sum.fsre.loginfirebase.dao.KorisnikDao
import ba.sum.fsre.loginfirebase.entity.Korisnik
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AuthRepository(
    private val authManager: FirebaseAuthManager,
    private val korisnikDao: KorisnikDao
) {

    data class AuthResult(val success: Boolean, val message: String? = null, val korisnik: Korisnik? = null)

    suspend fun login(email: String, password: String): AuthResult {
        val (ok, msg, uid) = awaitLogin(email, password)
        if (!ok || uid == null) return AuthResult(false, msg ?: "Login error")

        val local = korisnikDao.findByFirebaseUid(uid)
        return AuthResult(true, null, local)
    }

    suspend fun register(
        ime: String,
        prezime: String,
        email: String,
        telefon: String?,
        password: String
    ): AuthResult {
        val (ok, msg, uid) = awaitRegister(email, password)
        if (!ok || uid == null) return AuthResult(false, msg ?: "Register error")


        val existing = korisnikDao.findByFirebaseUid(uid)
        if (existing != null) return AuthResult(true, null, existing)

        val korisnik = Korisnik(
            ime = ime,
            prezime = prezime,
            email = email,
            telefon = telefon,
            firebaseUid = uid
        )

        return try {
            korisnikDao.insert(korisnik)
            val saved = korisnikDao.findByFirebaseUid(uid)
            AuthResult(true, null, saved)
        } catch (e: Exception) {
            AuthResult(false, e.message ?: "Room save error")
        }
    }

    fun logout() = authManager.logout()

    private suspend fun awaitLogin(email: String, password: String): Triple<Boolean, String?, String?> =
        suspendCancellableCoroutine { cont ->
            authManager.login(email, password) { success, message, uid ->
                if (cont.isActive) cont.resume(Triple(success, message, uid))
            }
        }

    private suspend fun awaitRegister(email: String, password: String): Triple<Boolean, String?, String?> =
        suspendCancellableCoroutine { cont ->
            authManager.register(email, password) { success, message, uid ->
                if (cont.isActive) cont.resume(Triple(success, message, uid))
            }
        }
}