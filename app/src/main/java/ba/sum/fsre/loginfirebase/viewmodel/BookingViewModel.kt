package ba.sum.fsre.loginfirebase.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ba.sum.fsre.loginfirebase.entity.Rezervacija
import ba.sum.fsre.loginfirebase.entity.Soba
import ba.sum.fsre.loginfirebase.repository.RepositoryProvider
import kotlinx.coroutines.launch

class BookingViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = RepositoryProvider.booking(app.applicationContext)

    val loading = MutableLiveData(false)
    val error = MutableLiveData<String?>(null)

    val availableRooms = MutableLiveData<List<Soba>>(emptyList())
    val reservations = MutableLiveData<List<Rezervacija>>(emptyList())

    fun loadAvailableRooms(smjestajId: Int, od: Long, do_: Long) {
        loading.value = true
        error.value = null

        viewModelScope.launch {
            val res = repo.availableRooms(smjestajId, od, do_)
            loading.value = false

            if (res.ok) availableRooms.value = res.data ?: emptyList()
            else error.value = res.message
        }
    }

    fun reserveWithPayment(
        korisnikId: Int,
        sobaId: Int,
        od: Long,
        do_: Long,
        ukupnaCijena: Double,
        nacinPlacanja: String,
        onSuccess: () -> Unit
    ) {
        loading.value = true
        error.value = null

        viewModelScope.launch {
            val res = repo.createReservationWithPayment(
                korisnikId = korisnikId,
                sobaId = sobaId,
                datumDolaska = od,
                datumOdlaska = do_,
                ukupnaCijena = ukupnaCijena,
                nacinPlacanja = nacinPlacanja
            )

            loading.value = false
            if (res.ok) onSuccess()
            else error.value = res.message
        }
    }

    fun loadUserReservations(korisnikId: Int) {
        loading.value = true
        error.value = null

        viewModelScope.launch {
            val res = repo.userReservations(korisnikId)
            loading.value = false

            if (res.ok) reservations.value = res.data ?: emptyList()
            else error.value = res.message
        }
    }

    fun loadAdminReservations() {
        loading.value = true
        error.value = null

        viewModelScope.launch {
            val res = repo.allReservationsNonAdmin()
            loading.value = false

            if (res.ok) reservations.value = res.data ?: emptyList()
            else error.value = res.message
        }
    }


    fun actionReservation(rezervacijaId: Int, korisnikId: Int, role: String) {
        loading.value = true
        error.value = null

        viewModelScope.launch {
            val res = if (role == "ADMIN") {
                repo.cancelReservationAdmin(rezervacijaId)
            } else {
                repo.cancelReservationUser(rezervacijaId, korisnikId)
            }

            loading.value = false

            if (res.ok) {
                if (role == "ADMIN") loadAdminReservations()
                else loadUserReservations(korisnikId)
            } else {
                error.value = res.message
            }
        }
    }
}