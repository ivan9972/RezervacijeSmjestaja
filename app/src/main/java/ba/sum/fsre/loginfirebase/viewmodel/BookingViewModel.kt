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
    val userReservations = MutableLiveData<List<Rezervacija>>(emptyList())

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
        onSuccess: (() -> Unit)? = null
    ) {
        loading.value = true
        error.value = null

        viewModelScope.launch {
            val res = repo.createReservationWithPayment(korisnikId, sobaId, od, do_, ukupnaCijena, nacinPlacanja)
            loading.value = false

            if (res.ok) onSuccess?.invoke()
            else error.value = res.message
        }
    }

    fun loadUserReservations(korisnikId: Int) {
        loading.value = true
        error.value = null

        viewModelScope.launch {
            val res = repo.userReservations(korisnikId)
            loading.value = false
            if (res.ok) userReservations.value = res.data ?: emptyList()
            else error.value = res.message
        }
    }

    fun cancelReservation(rezervacijaId: Int, korisnikId: Int) {
        loading.value = true
        error.value = null

        viewModelScope.launch {
            val res = repo.cancelReservation(rezervacijaId, korisnikId)
            loading.value = false

            if (res.ok) loadUserReservations(korisnikId)
            else error.value = res.message
        }
    }

    fun deleteCancelledReservation(rezervacijaId: Int, korisnikId: Int) {
        loading.value = true
        error.value = null

        viewModelScope.launch {
            val res = repo.deleteCancelledReservation(rezervacijaId, korisnikId)
            loading.value = false

            if (res.ok) loadUserReservations(korisnikId)
            else error.value = res.message
        }
    }
}