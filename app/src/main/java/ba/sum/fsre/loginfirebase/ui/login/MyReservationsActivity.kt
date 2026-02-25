package ba.sum.fsre.loginfirebase.ui.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ba.sum.fsre.loginfirebase.databinding.ActivityMyReservationsBinding
import ba.sum.fsre.loginfirebase.viewmodel.BookingViewModel

class MyReservationsActivity : AppCompatActivity() {

    private lateinit var b: ActivityMyReservationsBinding
    private val vm: BookingViewModel by viewModels()

    private var korisnikId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMyReservationsBinding.inflate(layoutInflater)
        setContentView(b.root)

        korisnikId = intent.getIntExtra("KORISNIK_ID", -1)
        if (korisnikId <= 0) {
            Toast.makeText(this, "Greška: korisnikId", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        b.btnBack.setOnClickListener { finish() }

        val adapter = RezervacijaAdapter(
            onCancel = { r ->
                vm.cancelReservation(r.id, korisnikId)
            },
            onDelete = { r ->
                vm.deleteCancelledReservation(r.id, korisnikId)
            }
        )

        b.rvRezervacije.layoutManager = LinearLayoutManager(this)
        b.rvRezervacije.adapter = adapter

        vm.loading.observe(this) { loading ->
            b.progress.visibility = if (loading) View.VISIBLE else View.GONE
        }

        vm.error.observe(this) { msg ->
            if (!msg.isNullOrBlank()) Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }

        vm.userReservations.observe(this) { list ->
            adapter.submit(list)
            b.tvCount.text = "Ukupno: ${list.size}"
        }

        vm.loadUserReservations(korisnikId)
    }
}