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

    companion object {
        private const val ADMIN_EMAIL = "dragan.barisic@gmail.com"
    }

    suspend fun login(email: String, password: String): AuthResult {
        val (ok, msg, uid) = awaitLogin(email, password)
        if (!ok || uid == null) return AuthResult(false, msg ?: "Login error")


        var local = korisnikDao.findByFirebaseUid(uid)


        if (local == null) {
            local = korisnikDao.findByEmail(email)
        }


        if (local == null) {
            return AuthResult(false, "Profil nije pronađen u aplikaciji. Registriraj se (ili kontaktiraj admina).")
        }


        if (!local.aktivan) {
            return AuthResult(false, "Račun je deaktiviran od administratora.")
        }


        val isAdminEmail = local.email.trim().lowercase() == ADMIN_EMAIL.lowercase()
        if (isAdminEmail && local.role != "ADMIN") {
            korisnikDao.setRole(local.id, "ADMIN")
            local = korisnikDao.findById(local.id) ?: local
        }

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

        // ako već postoji lokalno
        val existingByUid = korisnikDao.findByFirebaseUid(uid)
        if (existingByUid != null) return AuthResult(true, null, existingByUid)

        val existingByEmail = korisnikDao.findByEmail(email)
        if (existingByEmail != null) return AuthResult(true, null, existingByEmail)


        val role = if (email.trim().lowercase() == ADMIN_EMAIL.lowercase()) "ADMIN" else "USER"

        val korisnik = Korisnik(
            ime = ime,
            prezime = prezime,
            email = email,
            telefon = telefon,
            firebaseUid = uid,
            role = role,
            aktivan = true
        )

        return try {
            korisnikDao.insert(korisnik)
            val saved = korisnikDao.findByFirebaseUid(uid) ?: korisnikDao.findByEmail(email)
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