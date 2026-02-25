package ba.sum.fsre.loginfirebase.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import ba.sum.fsre.loginfirebase.database.DatabaseProvider
import ba.sum.fsre.loginfirebase.databinding.ActivityHomeBinding
import ba.sum.fsre.loginfirebase.entity.Korisnik
import ba.sum.fsre.loginfirebase.entity.Smjestaj
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    private lateinit var b: ActivityHomeBinding
    private var korisnikId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(b.root)

        korisnikId = intent.getIntExtra("KORISNIK_ID", -1)
        if (korisnikId <= 0) {
            Toast.makeText(this, "Greška: korisnik nije učitan.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val adapter = SmjestajAdapter { smjestaj ->
            val i = Intent(this, SmjestajDetailsActivity::class.java)
            i.putExtra("KORISNIK_ID", korisnikId)
            i.putExtra("SMJESTAJ_ID", smjestaj.id)
            startActivity(i)
        }

        b.rvSmjestaji.layoutManager = LinearLayoutManager(this)
        b.rvSmjestaji.adapter = adapter

        b.btnMyReservations.setOnClickListener {
            val i = Intent(this, MyReservationsActivity::class.java)
            i.putExtra("KORISNIK_ID", korisnikId)
            startActivity(i)
        }


        b.btnSearch.setOnClickListener {
            val grad = b.etGradFilter.text.toString().trim()
            if (grad.isBlank()) {
                Toast.makeText(this, "Upiši grad.", Toast.LENGTH_SHORT).show()
            } else {
                searchByGrad(adapter, grad)
            }
        }


        b.btnReset.setOnClickListener {
            b.etGradFilter.setText("")
            loadAll(adapter)
        }

        seedAndLoad(adapter)
    }

    private fun seedAndLoad(adapter: SmjestajAdapter) {
        val db = DatabaseProvider.get(this)

        lifecycleScope.launch {
            val smjestajDao = db.smjestajDao()
            val korisnikDao = db.korisnikDao()

            val finalList = withContext(Dispatchers.IO) {

                suspend fun ensureOwner(email: String, ime: String, prezime: String, uid: String): Int {
                    val existing = korisnikDao.findByEmail(email)
                    if (existing != null) return existing.id

                    korisnikDao.insert(
                        Korisnik(
                            ime = ime,
                            prezime = prezime,
                            email = email,
                            telefon = null,
                            firebaseUid = uid
                        )
                    )
                    return korisnikDao.findByEmail(email)!!.id
                }

                val owner1 = ensureOwner("owner1@seed.local", "Ana", "Vlasnik", "seed_owner_1")
                val owner2 = ensureOwner("owner2@seed.local", "Marko", "Vlasnik", "seed_owner_2")
                val owner3 = ensureOwner("owner3@seed.local", "Ivana", "Vlasnik", "seed_owner_3")
                val owner4 = ensureOwner("owner4@seed.local", "Petar", "Vlasnik", "seed_owner_4")
                val owner5 = ensureOwner("owner5@seed.local", "Ema", "Vlasnik", "seed_owner_5")
                val owner6 = ensureOwner("owner6@seed.local", "Luka", "Vlasnik", "seed_owner_6")

                // ✅ NEMA id=... OVDJE (Room sam generira Int id)
                val wanted = listOf(
                    Smjestaj(naziv = "Villa Mare",        opis = "Mirno mjesto uz more.",          grad = "Zadar",     adresa = "Obala 12",             vlasnikId = owner1),
                    Smjestaj(naziv = "Apartmani Centar",  opis = "U centru grada.",               grad = "Split",     adresa = "Riva 1",               vlasnikId = owner1),
                    Smjestaj(naziv = "Mountain Lodge",    opis = "Priroda i pogled.",             grad = "Mostar",    adresa = "Brdo bb",              vlasnikId = owner2),
                    Smjestaj(naziv = "City Rooms",        opis = "Praktično za putovanja.",       grad = "Zagreb",    adresa = "Ilica 45",             vlasnikId = owner2),
                    Smjestaj(naziv = "Green House",       opis = "Idealno za obitelj.",           grad = "Rijeka",    adresa = "Park 3",               vlasnikId = owner3),
                    Smjestaj(naziv = "Sunny Apartments",  opis = "Blizu plaže, top za ljeto.",    grad = "Makarska",  adresa = "Marina 8",             vlasnikId = owner3),
                    Smjestaj(naziv = "Old Town Studio",   opis = "U staroj jezgri.",              grad = "Dubrovnik", adresa = "Stradun 2",            vlasnikId = owner4),
                    Smjestaj(naziv = "Planinska Kuća",    opis = "Mir i tišina, idealno vikend.", grad = "Jablanica", adresa = "Jezero 1",             vlasnikId = owner4),
                    Smjestaj(naziv = "Seaside Panorama",  opis = "Pogled na more, parking.",      grad = "Šibenik",   adresa = "Luka 5",               vlasnikId = owner5),
                    Smjestaj(naziv = "Bjelašnica Chalet", opis = "Skijanje i planine.",           grad = "Sarajevo",  adresa = "Bjelašnica bb",        vlasnikId = owner5),
                    Smjestaj(naziv = "Pannonian Rooms",   opis = "Udobno i mirno.",               grad = "Osijek",    adresa = "Europske avenije 10",  vlasnikId = owner6),
                    Smjestaj(naziv = "Neretva Riverside", opis = "Uz rijeku, blizu centra.",      grad = "Čapljina",  adresa = "Neretvanska 7",        vlasnikId = owner6)
                )

                val existing = smjestajDao.findAll()
                val names = existing.map { it.naziv.trim().lowercase() }.toSet()

                wanted.forEach { s ->
                    if (!names.contains(s.naziv.trim().lowercase())) {
                        smjestajDao.insert(s)
                    }
                }

                smjestajDao.findAll()
            }

            adapter.submit(finalList)
            Toast.makeText(this@HomeActivity, "Smještaja u bazi: ${finalList.size}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchByGrad(adapter: SmjestajAdapter, grad: String) {
        val db = DatabaseProvider.get(this)
        lifecycleScope.launch {
            val list = withContext(Dispatchers.IO) {
                db.smjestajDao().searchByGrad(grad)
            }
            adapter.submit(list)
            Toast.makeText(this@HomeActivity, "Nađeno: ${list.size}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadAll(adapter: SmjestajAdapter) {
        val db = DatabaseProvider.get(this)
        lifecycleScope.launch {
            val list = withContext(Dispatchers.IO) {
                db.smjestajDao().findAll()
            }
            adapter.submit(list)
        }
    }
}