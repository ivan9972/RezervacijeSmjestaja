package ba.sum.fsre.loginfirebase.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ba.sum.fsre.loginfirebase.databinding.ActivityAdminPanelBinding
import com.google.firebase.auth.FirebaseAuth

class AdminPanelActivity : AppCompatActivity() {

    private lateinit var b: ActivityAdminPanelBinding
    private var korisnikId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityAdminPanelBinding.inflate(layoutInflater)
        setContentView(b.root)

        korisnikId = intent.getIntExtra("KORISNIK_ID", -1)
        val fullNameRaw = intent.getStringExtra("FULL_NAME") ?: ""

        if (korisnikId <= 0) {
            Toast.makeText(this, "Greška: admin nije učitan.", Toast.LENGTH_LONG).show()
            finish()
            return
        }


        val shownName = when {
            fullNameRaw.isBlank() -> "Administrator"
            fullNameRaw.trim().equals("korisnik", ignoreCase = true) -> "Administrator"
            fullNameRaw.trim().equals("user", ignoreCase = true) -> "Administrator"
            else -> fullNameRaw.trim()
        }


        b.tvTitle.text = "Dobrodošli, $shownName"

        b.btnUsers.setOnClickListener {
            val i = Intent(this, AdminUsersActivity::class.java)
            i.putExtra("KORISNIK_ID", korisnikId)
            i.putExtra("ROLE", "ADMIN")
            startActivity(i)
        }

        b.btnReservations.setOnClickListener {
            val i = Intent(this, AdminReservationsActivity::class.java)
            i.putExtra("KORISNIK_ID", korisnikId)
            i.putExtra("ROLE", "ADMIN")
            startActivity(i)
        }

        b.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val i = Intent(this, LoginActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }
    }
}