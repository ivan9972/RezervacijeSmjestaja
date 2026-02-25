package ba.sum.fsre.loginfirebase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ba.sum.fsre.loginfirebase.entity.Recenzija

@Dao
interface RecenzijaDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(r: Recenzija): Long

    @Query("SELECT * FROM recenzije WHERE smjestajId = :smjestajId")
    suspend fun findBySmjestaj(smjestajId: Int): List<Recenzija>

    @Query("SELECT * FROM recenzije WHERE korisnikId = :korisnikId")
    suspend fun findByKorisnik(korisnikId: Int): List<Recenzija>

    @Query("SELECT * FROM recenzije WHERE korisnikId = :korisnikId AND smjestajId = :smjestajId LIMIT 1")
    suspend fun findOne(korisnikId: Int, smjestajId: Int): Recenzija?
}