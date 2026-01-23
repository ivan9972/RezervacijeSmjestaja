package ba.sum.fsre.usermanagement

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    fun saveLogin(userId: Long) {
        prefs.edit()
            .putBoolean("isLoggedIn", true)
            .putLong("userId", userId)
            .apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean("isLoggedIn", false)
    fun getUserId(): Long = prefs.getLong("userId", -1)

    fun logout() {
        prefs.edit().clear().apply()
    }
}
