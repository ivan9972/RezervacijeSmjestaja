package ba.sum.fsre.loginfirebase.repository

import android.content.Context
import ba.sum.fsre.loginfirebase.auth.FirebaseAuthManager
import ba.sum.fsre.loginfirebase.database.DatabaseProvider

object RepositoryProvider {

    fun auth(context: Context): AuthRepository {
        val db = DatabaseProvider.get(context)
        return AuthRepository(
            authManager = FirebaseAuthManager(),
            korisnikDao = db.korisnikDao()
        )
    }

    fun booking(context: Context): BookingRepository {
        val db = DatabaseProvider.get(context)
        return BookingRepository(
            db = db
        )
    }
}