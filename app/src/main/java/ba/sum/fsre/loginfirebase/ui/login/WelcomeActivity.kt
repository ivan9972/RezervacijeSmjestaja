package ba.sum.fsre.loginfirebase.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(b.root)


        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid.isNullOrBlank()) {
            Toast.makeText(this, "Nisi prijavljen.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val db = DatabaseProvider.get(this)

        lifecycleScope.launch {
            val korisnik = withContext(Dispatchers.IO) {
                db.korisnikDao().findByFirebaseUid(uid)
            }

            if (korisnik == null) {
                Toast.makeText(this@WelcomeActivity, "Korisnik nije pronađen u bazi.", Toast.LENGTH_LONG).show()
                startActivity(Intent(this@WelcomeActivity, LoginActivity::class.java))
                finish()
                return@launch
            }

            korisnikId = korisnik.id


            b.textWelcome.text = "Welcome, ${korisnik.ime} ${korisnik.prezime}"

            val lista = listOf("${korisnik.ime} ${korisnik.prezime} (${korisnik.email})")
            b.listProfiles.adapter = ArrayAdapter(
                this@WelcomeActivity,
                android.R.layout.simple_list_item_1,
                lista
            )


            b.listProfiles.setOnItemClickListener { _, _, _, _ ->
                val i = Intent(this@WelcomeActivity, HomeActivity::class.java)
                i.putExtra("KORISNIK_ID", korisnikId)
                startActivity(i)
            }
        }

        b.buttonLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val i = Intent(this, LoginActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }
    }
}