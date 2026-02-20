package ba.sum.fsre.loginfirebase.dao

import androidx.room.*
import ba.sum.fsre.loginfirebase.entity.Placanje

@Dao
interface PlacanjeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(placanje: Placanje)

    @Query("SELECT * FROM placanja WHERE rezervacijaId = :rezervacijaId")
    suspend fun findByRezervacija(rezervacijaId: Int): List<Placanje>
}