package ba.sum.fsre.loginfirebase.dao

import androidx.room.*
import ba.sum.fsre.loginfirebase.entity.Rezervacija

@Dao
interface RezervacijaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rezervacija: Rezervacija)

    @Query("SELECT * FROM rezervacije WHERE korisnikId = :korisnikId")
    suspend fun findByKorisnik(korisnikId: Int): List<Rezervacija>

    @Query("SELECT * FROM rezervacije WHERE sobaId = :sobaId")
    suspend fun findBySoba(sobaId: Int): List<Rezervacija>
}