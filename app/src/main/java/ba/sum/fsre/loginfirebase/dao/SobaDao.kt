package ba.sum.fsre.loginfirebase.dao

import androidx.room.*
import ba.sum.fsre.loginfirebase.entity.Soba

@Dao
interface SobaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(soba: Soba)

    @Query("SELECT * FROM sobe WHERE smjestajId = :smjestajId")
    suspend fun findBySmjestaj(smjestajId: Int): List<Soba>

    @Query("SELECT * FROM sobe WHERE id = :id")
    suspend fun findById(id: Int): Soba?
}