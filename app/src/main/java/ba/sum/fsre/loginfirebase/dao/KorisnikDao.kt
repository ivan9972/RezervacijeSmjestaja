package ba.sum.fsre.loginfirebase.dao

import androidx.room.*
import ba.sum.fsre.loginfirebase.entity.Korisnik

@Dao
interface KorisnikDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(korisnik: Korisnik)

    @Query("SELECT * FROM korisnici WHERE firebaseUid = :uid LIMIT 1")
    suspend fun findByFirebaseUid(uid: String): Korisnik?
}