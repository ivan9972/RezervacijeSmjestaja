package ba.sum.fsre.loginfirebase.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ba.sum.fsre.loginfirebase.databinding.ActivityMyReservationsBinding
import ba.sum.fsre.loginfirebase.viewmodel.BookingViewModel
import com.google.firebase.auth.FirebaseAuth

class MyReservationsActivity : AppCompatActivity() {

    private lateinit var b: ActivityMyReservationsBinding
    private val vm: BookingViewModel by viewModels()

    private var korisnikId: Int = -1
    private var role: String = "USER"
    private var fullName: String = "Korisnik"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMyReservationsBinding.inflate(layoutInflater)
        setContentView(b.root)

        korisnikId = intent.getIntExtra("KORISNIK_ID", -1)
        role = intent.getStringExtra("ROLE") ?: "USER"
        fullName = intent.getStringExtra("FULL_NAME") ?: "Korisnik"

        if (korisnikId <= 0) {
            Toast.makeText(this, "Nedostaje korisnikId.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        b.tvTitle.text = if (role == "ADMIN") "Rezervacije" else "Moje rezervacije"

        val adapter = RezervacijaAdapter(
            isAdmin = (role == "ADMIN"),
            onAction = { rez ->
                vm.actionReservation(rez.id, korisnikId, role)
            }
        )

        b.rvRezervacije.layoutManager = LinearLayoutManager(this)
        b.rvRezervacije.adapter = adapter

        vm.loading.observe(this) { loading ->
            b.progress.visibility = if (loading == true) View.VISIBLE else View.GONE
        }

        vm.error.observe(this) { msg ->
            if (!msg.isNullOrBlank()) Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }

        vm.reservations.observe(this) { list ->
            val safe = list ?: emptyList()
            adapter.submitList(safe)
            b.tvCount.text = "Ukupno: ${safe.size}"
        }

        if (role == "ADMIN") vm.loadAdminReservations()
        else vm.loadUserReservations(korisnikId)

        b.btnBack.setOnClickListener {
            val i = Intent(this, WelcomeActivity::class.java)
            i.putExtra("KORISNIK_ID", korisnikId)
            i.putExtra("ROLE", role)
            i.putExtra("FULL_NAME", fullName)
            startActivity(i)
            finish()
        }

        b.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val i = Intent(this, LoginActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }
    }
}