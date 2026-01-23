package ba.sum.fsre.usermanagement

import java.security.MessageDigest


object PasswordHasher {
    fun sha256(text: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(text.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

class AuthRepository(private val dao: UserProfileDao) {

    suspend fun register(fullName: String, email: String, password: String): Result<Long> {
        val name = fullName.trim()
        val mail = email.trim().lowercase()

        if (name.isBlank()) return Result.failure(IllegalArgumentException("Ime je obavezno"))
        if (mail.isBlank()) return Result.failure(IllegalArgumentException("Email je obavezan"))
        if (password.length < 6) return Result.failure(IllegalArgumentException("Lozinka mora imati min. 6 znakova"))

        val existing = dao.findByEmail(mail)
        if (existing != null) return Result.failure(IllegalStateException("Email je već registriran"))

        val profile = UserProfile(
            fullName = name,
            email = mail,
            passwordHash = PasswordHasher.sha256(password)
        )

        val id = dao.insert(profile)
        return Result.success(id)
    }

    suspend fun login(email: String, password: String): Result<UserProfile> {
        val mail = email.trim().lowercase()
        if (mail.isBlank()) return Result.failure(IllegalArgumentException("Email je obavezan"))
        if (password.isBlank()) return Result.failure(IllegalArgumentException("Lozinka je obavezna"))

        val user = dao.findByEmail(mail) ?: return Result.failure(IllegalStateException("Korisnik ne postoji"))

        val hash = PasswordHasher.sha256(password)
        if (user.passwordHash != hash) return Result.failure(IllegalArgumentException("Pogrešna lozinka"))

        dao.updateLastLogin(user.id)
        return Result.success(user)
    }
}