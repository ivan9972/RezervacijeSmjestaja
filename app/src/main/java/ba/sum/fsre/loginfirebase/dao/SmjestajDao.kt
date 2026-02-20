package ba.sum.fsre.loginfirebase.dao

import androidx.room.*
import ba.sum.fsre.loginfirebase.entity.Smjestaj

@Dao
interface SmjestajDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(smjestaj: Smjestaj)

    @Query("SELECT * FROM smjestaji WHERE id = :id")
    suspend fun findById(id: Int): Smjestaj?

    @Query("SELECT * FROM smjestaji WHERE vlasnikId = :vlasnikId")
    suspend fun findByVlasnik(vlasnikId: Int): List<Smjestaj>
}