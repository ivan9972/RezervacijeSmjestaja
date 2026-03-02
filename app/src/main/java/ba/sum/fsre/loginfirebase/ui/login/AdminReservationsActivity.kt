package ba.sum.fsre.loginfirebase.ui.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ba.sum.fsre.loginfirebase.databinding.ActivityMyReservationsBinding
import ba.sum.fsre.loginfirebase.viewmodel.BookingViewModel

class AdminReservationsActivity : AppCompatActivity() {

    private lateinit var b: ActivityMyReservationsBinding
    private val vm: BookingViewModel by viewModels()

    private var adminId: Int = -1
    private val role = "ADMIN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMyReservationsBinding.inflate(layoutInflater)
        setContentView(b.root)

        adminId = intent.getIntExtra("KORISNIK_ID", -1)
        if (adminId <= 0) {
            Toast.makeText(this, "Greška: adminId", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        b.tvTitle.text = "Rezervacije"
        b.btnBack.setOnClickListener { finish() }

        val adapter = RezervacijaAdapter(
            isAdmin = true,
            onAction = { r -> vm.actionReservation(r.id, adminId, role) }
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

        vm.loadAdminReservations()
    }
}