package ba.sum.fsre.loginfirebase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ba.sum.fsre.loginfirebase.entity.Placanje

@Dao
interface PlacanjeDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(p: Placanje): Long

    @Query("SELECT * FROM placanja WHERE rezervacijaId = :rezervacijaId LIMIT 1")
    suspend fun findByRezervacija(rezervacijaId: Int): Placanje?
}