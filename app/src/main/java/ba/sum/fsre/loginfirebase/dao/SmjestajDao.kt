package ba.sum.fsre.loginfirebase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ba.sum.fsre.loginfirebase.entity.Smjestaj

@Dao
interface SmjestajDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(smjestaj: Smjestaj): Long

    @Query("SELECT * FROM smjestaji WHERE id = :id")
    suspend fun findById(id: Int): Smjestaj?

    @Query("SELECT * FROM smjestaji WHERE vlasnikId = :vlasnikId")
    suspend fun findByVlasnik(vlasnikId: Int): List<Smjestaj>

    @Query("SELECT * FROM smjestaji")
    suspend fun findAll(): List<Smjestaj>

    @Query("SELECT * FROM smjestaji WHERE grad LIKE '%' || :grad || '%'")
    suspend fun searchByGrad(grad: String): List<Smjestaj>

    // ✅ za seed provjeru
    @Query("SELECT COUNT(*) FROM smjestaji")
    suspend fun countAll(): Int
}