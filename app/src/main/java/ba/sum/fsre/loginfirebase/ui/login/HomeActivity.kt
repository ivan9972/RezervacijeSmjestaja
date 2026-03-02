package ba.sum.fsre.loginfirebase.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import ba.sum.fsre.loginfirebase.database.DatabaseProvider
import ba.sum.fsre.loginfirebase.databinding.ActivityHomeBinding
import ba.sum.fsre.loginfirebase.entity.Smjestaj
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    private lateinit var b: ActivityHomeBinding
    private lateinit var adapter: SmjestajAdapter

    private var korisnikId: Int = -1
    private var role: String = "USER"
    private var fullName: String = ""

    private val allItems = mutableListOf<Smjestaj>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(b.root)


        korisnikId = intent.getIntExtra("KORISNIK_ID", -1)
        role = intent.getStringExtra("ROLE") ?: "USER"
        fullName = intent.getStringExtra("FULL_NAME") ?: ""

        if (korisnikId <= 0) {
            Toast.makeText(this, "Greška: korisnikId", Toast.LENGTH_LONG).show()
            finish()
            return
        }


        adapter = SmjestajAdapter { smjestaj ->
            val i = Intent(this, SmjestajDetailsActivity::class.java)
            i.putExtra("SMJESTAJ_ID", smjestaj.id)
            i.putExtra("KORISNIK_ID", korisnikId)
            i.putExtra("ROLE", role)
            i.putExtra("FULL_NAME", fullName)
            startActivity(i)
        }

        b.rvSmjestaji.layoutManager = LinearLayoutManager(this)
        b.rvSmjestaji.adapter = adapter


        b.btnBack.setOnClickListener {
            val i = Intent(this, WelcomeActivity::class.java)
            i.putExtra("KORISNIK_ID", korisnikId)
            i.putExtra("ROLE", role)
            i.putExtra("FULL_NAME", fullName)
            startActivity(i)
            finish()
        }

        b.btnMyReservations.setOnClickListener {
            val i = Intent(this, MyReservationsActivity::class.java)
            i.putExtra("KORISNIK_ID", korisnikId)
            i.putExtra("ROLE", role)
            i.putExtra("FULL_NAME", fullName)
            startActivity(i)
        }


        b.btnFilter.setOnClickListener {
            val city = b.etCityFilter.text.toString().trim()
            if (city.isEmpty()) {
                adapter.submit(allItems)
                b.tvFilterInfo.text = ""
            } else {
                val filtered = allItems.filter {
                    it.grad.contains(city, ignoreCase = true)
                }
                adapter.submit(filtered)
                b.tvFilterInfo.text = "Filtrirano po: $city"
            }
        }

        b.btnClearFilter.setOnClickListener {
            b.etCityFilter.setText("")
            adapter.submit(allItems)
            b.tvFilterInfo.text = ""
        }

        loadSmjestaji()
    }

    private fun loadSmjestaji() {
        val db = DatabaseProvider.get(this)

        lifecycleScope.launch {
            val list = withContext(Dispatchers.IO) {
                db.smjestajDao().findAll()
            }

            allItems.clear()
            allItems.addAll(list)
            adapter.submit(allItems)
        }
    }
}