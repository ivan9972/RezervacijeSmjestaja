package ba.sum.fsre.loginfirebase.repository

import androidx.room.withTransaction
import ba.sum.fsre.loginfirebase.database.AppDatabase
import ba.sum.fsre.loginfirebase.entity.Placanje
import ba.sum.fsre.loginfirebase.entity.Rezervacija
import ba.sum.fsre.loginfirebase.entity.Soba

class BookingRepository(private val db: AppDatabase) {

    data class Result<T>(val ok: Boolean, val message: String? = null, val data: T? = null)

    suspend fun availableRooms(smjestajId: Int, datumDolaska: Long, datumOdlaska: Long): Result<List<Soba>> {
        if (datumDolaska >= datumOdlaska) return Result(false, "Datum dolaska mora biti prije datuma odlaska.")
        return try {
            val rooms = db.sobaDao().findAvailableRooms(smjestajId, datumDolaska, datumOdlaska)
            Result(true, data = rooms)
        } catch (e: Exception) {
            Result(false, e.message ?: "Greška kod učitavanja soba.")
        }
    }

    suspend fun createReservationWithPayment(
        korisnikId: Int,
        sobaId: Int,
        datumDolaska: Long,
        datumOdlaska: Long,
        ukupnaCijena: Double,
        nacinPlacanja: String
    ): Result<Int> {

        if (datumDolaska >= datumOdlaska) return Result(false, "Datum dolaska mora biti prije datuma odlaska.")
        if (ukupnaCijena <= 0.0) return Result(false, "Ukupna cijena nije ispravna.")

        return try {
            var newRezId = -1

            db.withTransaction {
                val overlap = db.rezervacijaDao().countOverlaps(sobaId, datumDolaska, datumOdlaska)
                if (overlap > 0) throw IllegalStateException("Soba nije dostupna za odabrane datume.")

                val rez = Rezervacija(
                    korisnikId = korisnikId,
                    sobaId = sobaId,
                    datumDolaska = datumDolaska,
                    datumOdlaska = datumOdlaska,
                    ukupnaCijena = ukupnaCijena,
                    status = "CONFIRMED"
                )

                val rezId = db.rezervacijaDao().insert(rez).toInt()
                newRezId = rezId

                val pay = Placanje(
                    rezervacijaId = rezId,
                    iznos = ukupnaCijena,
                    datumPlacanja = System.currentTimeMillis(),
                    nacinPlacanja = nacinPlacanja,
                    status = "PAID"
                )
                db.placanjeDao().insert(pay)
            }

            Result(true, data = newRezId)
        } catch (e: Exception) {
            Result(false, e.message ?: "Greška prilikom rezervacije.")
        }
    }

    suspend fun userReservations(korisnikId: Int): Result<List<Rezervacija>> {
        return try {
            Result(true, data = db.rezervacijaDao().findByKorisnik(korisnikId))
        } catch (e: Exception) {
            Result(false, e.message ?: "Greška kod učitavanja rezervacija.")
        }
    }


    suspend fun cancelReservation(rezervacijaId: Int, korisnikId: Int): Result<Unit> {
        return try {
            val updated = db.rezervacijaDao().cancelByIdAndUser(rezervacijaId, korisnikId)
            if (updated == 0) Result(false, "Ne možeš otkazati tuđu rezervaciju.")
            else Result(true)
        } catch (e: Exception) {
            Result(false, e.message ?: "Greška kod otkazivanja.")
        }
    }


    suspend fun deleteCancelledReservation(rezervacijaId: Int, korisnikId: Int): Result<Unit> {
        return try {
            val deleted = db.rezervacijaDao().deleteCancelledByIdAndUser(rezervacijaId, korisnikId)
            if (deleted == 0) Result(false, "Možeš obrisati samo svoju otkazanu rezervaciju.")
            else Result(true)
        } catch (e: Exception) {
            Result(false, e.message ?: "Greška kod brisanja.")
        }
    }
}