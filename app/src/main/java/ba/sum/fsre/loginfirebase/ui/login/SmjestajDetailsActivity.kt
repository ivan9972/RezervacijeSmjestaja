package ba.sum.fsre.loginfirebase.ui.login

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import ba.sum.fsre.loginfirebase.database.DatabaseProvider
import ba.sum.fsre.loginfirebase.databinding.ActivitySmjestajDetailsBinding
import ba.sum.fsre.loginfirebase.entity.Smjestaj
import ba.sum.fsre.loginfirebase.entity.Soba
import ba.sum.fsre.loginfirebase.viewmodel.BookingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class SmjestajDetailsActivity : AppCompatActivity() {

    private lateinit var b: ActivitySmjestajDetailsBinding
    private val vm: BookingViewModel by viewModels()

    private var korisnikId = -1
    private var smjestajId = -1
    private var selectedRoom: Soba? = null

    private val fmt = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private var fromMillis: Long? = null
    private var toMillis: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySmjestajDetailsBinding.inflate(layoutInflater)
        setContentView(b.root)

        korisnikId = intent.getIntExtra("KORISNIK_ID", -1)
        smjestajId = intent.getIntExtra("SMJESTAJ_ID", -1)

        if (korisnikId <= 0 || smjestajId <= 0) {
            Toast.makeText(this, "Nedostaju podaci.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        b.btnBack.setOnClickListener { finish() }


        lockEditTextForPicker(b.etFrom)
        lockEditTextForPicker(b.etTo)

        b.etFrom.setOnClickListener {
            pickDate { millis, text ->
                fromMillis = millis
                b.etFrom.setText(text)
                recalcTotal()
            }
        }

        b.etTo.setOnClickListener {
            pickDate { millis, text ->
                toMillis = millis
                b.etTo.setText(text)
                recalcTotal()
            }
        }


        b.spPayment.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Kartica", "Gotovina")
        )

        val sobaAdapter = SobaAdapter { soba ->
            selectedRoom = soba
            b.tvSelectedRoom.text =
                "Odabrano: ${soba.tip} • Soba ${soba.brojSobe} • %.2f € / noć".format(soba.cijenaPoNoci)
            recalcTotal()
        }

        b.rvSobe.layoutManager = LinearLayoutManager(this)
        b.rvSobe.adapter = sobaAdapter

        b.btnLoadAvailable.setOnClickListener {
            val (od, do_) = readDatesOrShowError() ?: return@setOnClickListener
            vm.loadAvailableRooms(smjestajId, od, do_)
        }

        b.btnReserve.setOnClickListener {
            val room = selectedRoom ?: run {
                Toast.makeText(this, "Odaberi sobu.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val (od, do_) = readDatesOrShowError() ?: return@setOnClickListener

            val nights = countNights(od, do_)
            val total = nights * room.cijenaPoNoci

            val payText = b.spPayment.selectedItem?.toString() ?: "Kartica"
            val payCode = if (payText.lowercase().contains("got")) "CASH" else "CARD"

            vm.reserveWithPayment(
                korisnikId = korisnikId,
                sobaId = room.id,
                od = od,
                do_ = do_,
                ukupnaCijena = total,
                nacinPlacanja = payCode,
                onSuccess = {
                    runOnUiThread {
                        Toast.makeText(this, "Rezervacija uspješna!", Toast.LENGTH_SHORT).show()
                        val i = Intent(this, MyReservationsActivity::class.java)
                        i.putExtra("KORISNIK_ID", korisnikId)
                        startActivity(i)
                        finish()
                    }
                }
            )
        }

        vm.loading.observe(this) { loading ->
            b.progress.visibility = if (loading) View.VISIBLE else View.GONE
            b.btnReserve.isEnabled = !loading
            b.btnLoadAvailable.isEnabled = !loading
        }

        vm.error.observe(this) { msg ->
            if (!msg.isNullOrBlank()) Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }

        vm.availableRooms.observe(this) { rooms ->
            sobaAdapter.submit(rooms)
            b.tvRoomsCount.text = "Dostupne sobe: ${rooms.size}"
        }

        loadSmjestajAndSeedRooms(sobaAdapter)
    }

    private fun lockEditTextForPicker(et: EditText) {
        et.inputType = InputType.TYPE_NULL
        et.keyListener = null
        et.isFocusable = false
        et.isFocusableInTouchMode = false
    }

    private fun pickDate(onPicked: (millis: Long, display: String) -> Unit) {
        val now = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, y, m, d ->
                val c = Calendar.getInstance()
                c.set(Calendar.YEAR, y)
                c.set(Calendar.MONTH, m)
                c.set(Calendar.DAY_OF_MONTH, d)
                c.set(Calendar.HOUR_OF_DAY, 0)
                c.set(Calendar.MINUTE, 0)
                c.set(Calendar.SECOND, 0)
                c.set(Calendar.MILLISECOND, 0)
                onPicked(c.timeInMillis, fmt.format(c.time))
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun readDatesOrShowError(): Pair<Long, Long>? {
        val od = fromMillis
        val do_ = toMillis

        if (od == null || do_ == null) {
            Toast.makeText(this, "Odaberi dolazak i odlazak (klikni na polja).", Toast.LENGTH_LONG).show()
            return null
        }
        if (od >= do_) {
            Toast.makeText(this, "Datum dolaska mora biti prije odlaska.", Toast.LENGTH_LONG).show()
            return null
        }
        return od to do_
    }

    private fun countNights(od: Long, do_: Long): Long {
        val nights = TimeUnit.MILLISECONDS.toDays(do_ - od)
        return if (nights <= 0) 1 else nights
    }

    private fun recalcTotal() {
        val room = selectedRoom ?: run {
            b.tvTotal.text = "Ukupno: —"
            return
        }
        val pair = readDatesOrShowError() ?: run {
            b.tvTotal.text = "Ukupno: —"
            return
        }

        val nights = countNights(pair.first, pair.second)
        val total = nights * room.cijenaPoNoci
        b.tvTotal.text = "Ukupno: $nights noći • %.2f €".format(total)
    }

    private fun loadSmjestajAndSeedRooms(sobaAdapter: SobaAdapter) {
        val db = DatabaseProvider.get(this)

        lifecycleScope.launch {
            val sm: Smjestaj? = withContext(Dispatchers.IO) { db.smjestajDao().findById(smjestajId) }
            if (sm == null) {
                Toast.makeText(this@SmjestajDetailsActivity, "Smještaj ne postoji.", Toast.LENGTH_LONG).show()
                finish()
                return@launch
            }

            b.tvTitle.text = sm.naziv
            b.tvLocation.text = "${sm.grad} • ${sm.adresa}"
            b.tvDesc.text = sm.opis

            val sobaDao = db.sobaDao()
            val rooms = withContext(Dispatchers.IO) { sobaDao.findBySmjestaj(smjestajId) }


            if (rooms.isEmpty()) {
                withContext(Dispatchers.IO) {
                    sobaDao.insert(Soba(smjestajId = smjestajId, brojSobe = "101", tip = "Studio",       kapacitet = 2, cijenaPoNoci = 55.0,  dostupna = true))
                    sobaDao.insert(Soba(smjestajId = smjestajId, brojSobe = "102", tip = "Double",       kapacitet = 2, cijenaPoNoci = 70.0,  dostupna = true))
                    sobaDao.insert(Soba(smjestajId = smjestajId, brojSobe = "103", tip = "Twin",         kapacitet = 2, cijenaPoNoci = 72.0,  dostupna = true))

                    sobaDao.insert(Soba(smjestajId = smjestajId, brojSobe = "201", tip = "Family",       kapacitet = 4, cijenaPoNoci = 95.0,  dostupna = true))
                    sobaDao.insert(Soba(smjestajId = smjestajId, brojSobe = "202", tip = "Apartment",    kapacitet = 5, cijenaPoNoci = 120.0, dostupna = true))
                    sobaDao.insert(Soba(smjestajId = smjestajId, brojSobe = "203", tip = "Apartment XL", kapacitet = 6, cijenaPoNoci = 135.0, dostupna = true))

                    sobaDao.insert(Soba(smjestajId = smjestajId, brojSobe = "301", tip = "Suite",        kapacitet = 4, cijenaPoNoci = 150.0, dostupna = true))
                    sobaDao.insert(Soba(smjestajId = smjestajId, brojSobe = "302", tip = "Suite Deluxe", kapacitet = 4, cijenaPoNoci = 175.0, dostupna = true))
                }
            }

            val after = withContext(Dispatchers.IO) { sobaDao.findBySmjestaj(smjestajId) }
            sobaAdapter.submit(after)
            b.tvRoomsCount.text = "Sobe: ${after.size}"
        }
    }
}