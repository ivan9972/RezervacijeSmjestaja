package ba.sum.fsre.loginfirebase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ba.sum.fsre.loginfirebase.entity.Korisnik

@Dao
interface KorisnikDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(korisnik: Korisnik): Long

    @Update
    suspend fun update(korisnik: Korisnik)

    @Query("SELECT * FROM korisnici WHERE firebaseUid = :uid LIMIT 1")
    suspend fun findByFirebaseUid(uid: String): Korisnik?

    @Query("SELECT * FROM korisnici WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): Korisnik?

    @Query("SELECT * FROM korisnici")
    suspend fun findAll(): List<Korisnik>
}