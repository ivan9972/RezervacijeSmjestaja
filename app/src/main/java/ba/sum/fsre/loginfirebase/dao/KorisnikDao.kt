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

    @Query("SELECT * FROM korisnici WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): Korisnik?


    @Query("""
        SELECT * FROM korisnici
        WHERE role = 'USER'
          AND aktivan = 1
          AND id != :adminId
        ORDER BY id DESC
    """)
    suspend fun findAllActiveUsers(adminId: Int): List<Korisnik>


    @Query("UPDATE korisnici SET aktivan = 0 WHERE id = :id AND role = 'USER'")
    suspend fun deactivateUserById(id: Int): Int

    @Query("UPDATE korisnici SET aktivan = 1 WHERE id = :id AND role = 'USER'")
    suspend fun activateUserById(id: Int): Int


    @Query("UPDATE korisnici SET role = :role WHERE id = :id")
    suspend fun setRole(id: Int, role: String): Int

}