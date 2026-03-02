package ba.sum.fsre.loginfirebase.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import ba.sum.fsre.loginfirebase.database.DatabaseProvider
import ba.sum.fsre.loginfirebase.databinding.ActivityWelcomeBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WelcomeActivity : AppCompatActivity() {

    private lateinit var b: ActivityWelcomeBinding
    private var korisnikId: Int = -1
    private var role: String = "USER"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(b.root)

        korisnikId = intent.getIntExtra("KORISNIK_ID", -1)
        role = intent.getStringExtra("ROLE") ?: "USER"

        if (korisnikId <= 0) {
            Toast.makeText(this, "Greška: korisnik", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        loadAndShowUserInfo(korisnikId)

        b.btnContinue.setOnClickListener {
            val i = Intent(this, HomeActivity::class.java)
            i.putExtra("KORISNIK_ID", korisnikId)
            i.putExtra("ROLE", role)
            startActivity(i)
        }

        b.btnMyReservations.setOnClickListener {
            val i = Intent(this, MyReservationsActivity::class.java)
            i.putExtra("KORISNIK_ID", korisnikId)
            i.putExtra("ROLE", role)
            startActivity(i)
        }

        b.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val i = Intent(this, LoginActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }
    }

    private fun loadAndShowUserInfo(id: Int) {
        val db = DatabaseProvider.get(this)

        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                val k = db.korisnikDao().findById(id)

                val fullName = if (k != null) {
                    listOf(k.ime, k.prezime)
                        .filter { it.isNotBlank() }
                        .joinToString(" ")
                        .ifBlank { k.email.substringBefore("@") }
                } else {
                    "Korisnik"
                }

                val rezCount = db.rezervacijaDao().countByKorisnik(id)

                Pair(fullName, rezCount)
            }

            b.tvWelcome.text = "Dobrodošli, ${result.first}"
            b.tvReservationsCount.text = "Ukupno rezervacija: ${result.second}"
        }
    }
}