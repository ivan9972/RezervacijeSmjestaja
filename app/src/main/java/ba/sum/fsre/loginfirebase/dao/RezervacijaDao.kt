package ba.sum.fsre.loginfirebase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ba.sum.fsre.loginfirebase.entity.Rezervacija

@Dao
interface RezervacijaDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(rezervacija: Rezervacija): Long

    @Update
    suspend fun update(rezervacija: Rezervacija)

    @Query("SELECT * FROM rezervacije WHERE korisnikId = :korisnikId ORDER BY id DESC")
    suspend fun findByKorisnik(korisnikId: Int): List<Rezervacija>

    @Query("SELECT * FROM rezervacije WHERE sobaId = :sobaId")
    suspend fun findBySoba(sobaId: Int): List<Rezervacija>

    // overlap
    @Query("""
        SELECT COUNT(*) FROM rezervacije
        WHERE sobaId = :sobaId
          AND status != 'CANCELLED'
          AND (datumDolaska < :datumOdlaska AND datumOdlaska > :datumDolaska)
    """)
    suspend fun countOverlaps(
        sobaId: Int,
        datumDolaska: Long,
        datumOdlaska: Long
    ): Int

    // ✅ otkaži samo ako je korisnik vlasnik
    @Query("UPDATE rezervacije SET status='CANCELLED' WHERE id=:rezId AND korisnikId=:korisnikId")
    suspend fun cancelByIdAndUser(rezId: Int, korisnikId: Int): Int

    // ✅ obriši samo ako je CANCELLED i korisnik vlasnik
    @Query("DELETE FROM rezervacije WHERE id=:rezId AND korisnikId=:korisnikId AND status='CANCELLED'")
    suspend fun deleteCancelledByIdAndUser(rezId: Int, korisnikId: Int): Int
}