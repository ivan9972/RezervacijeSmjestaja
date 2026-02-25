package ba.sum.fsre.loginfirebase.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseAuthManager {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun currentUser(): FirebaseUser? = auth.currentUser

    fun login(
        email: String,
        password: String,
        onResult: (success: Boolean, message: String?, uid: String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onResult(true, null, auth.currentUser?.uid)
                } else {
                    onResult(false, it.exception?.message, null)
                }
            }
    }

    fun register(
        email: String,
        password: String,
        onResult: (success: Boolean, message: String?, uid: String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onResult(true, null, auth.currentUser?.uid)
                } else {
                    onResult(false, it.exception?.message, null)
                }
            }
    }

    fun logout() {
        auth.signOut()
    }
}