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

    @Query("SELECT * FROM rezervacije WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): Rezervacija?


    @Query(
        """
        SELECT r.* FROM rezervacije r
        INNER JOIN korisnici k ON k.id = r.korisnikId
        WHERE k.role != 'ADMIN'
        ORDER BY r.id DESC
        """
    )
    suspend fun findAllNonAdmin(): List<Rezervacija>

    @Query(
        """
        SELECT COUNT(*) FROM rezervacije
        WHERE sobaId = :sobaId
          AND status != 'CANCELLED'
          AND (datumDolaska < :datumOdlaska AND datumOdlaska > :datumDolaska)
        """
    )
    suspend fun countOverlaps(
        sobaId: Int,
        datumDolaska: Long,
        datumOdlaska: Long
    ): Int

    @Query("UPDATE rezervacije SET status = 'CANCELLED' WHERE id = :rezId AND korisnikId = :korisnikId")
    suspend fun cancelByIdAndUser(rezId: Int, korisnikId: Int): Int


    @Query("DELETE FROM rezervacije WHERE id = :rezId")
    suspend fun deleteByIdAdmin(rezId: Int): Int

    @Query("SELECT COUNT(*) FROM rezervacije WHERE korisnikId = :korisnikId")
    suspend fun countByKorisnik(korisnikId: Int): Int
}