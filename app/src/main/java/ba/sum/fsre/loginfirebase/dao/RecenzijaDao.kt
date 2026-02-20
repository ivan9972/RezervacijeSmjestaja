package ba.sum.fsre.loginfirebase.dao

import androidx.room.*
import ba.sum.fsre.loginfirebase.entity.Recenzija

@Dao
interface RecenzijaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recenzija: Recenzija)

    @Query("SELECT * FROM recenzije WHERE smjestajId = :smjestajId")
    suspend fun findBySmjestaj(smjestajId: Int): List<Recenzija>

    @Query("SELECT * FROM recenzije WHERE korisnikId = :korisnikId")
    suspend fun findByKorisnik(korisnikId: Int): List<Recenzija>
}