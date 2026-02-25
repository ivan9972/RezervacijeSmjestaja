package ba.sum.fsre.loginfirebase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ba.sum.fsre.loginfirebase.entity.Soba

@Dao
interface SobaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(soba: Soba): Long

    @Query("SELECT * FROM sobe WHERE smjestajId = :smjestajId")
    suspend fun findBySmjestaj(smjestajId: Int): List<Soba>

    @Query("SELECT * FROM sobe WHERE id = :id")
    suspend fun findById(id: Int): Soba?

    // ✅ dostupne sobe
    @Query("""
        SELECT * FROM sobe
        WHERE smjestajId = :smjestajId
          AND dostupna = 1
          AND id NOT IN (
            SELECT sobaId FROM rezervacije
            WHERE status != 'CANCELLED'
              AND (datumDolaska < :datumOdlaska AND datumOdlaska > :datumDolaska)
          )
    """)
    suspend fun findAvailableRooms(
        smjestajId: Int,
        datumDolaska: Long,
        datumOdlaska: Long
    ): List<Soba>

    // ✅ za seed provjeru
    @Query("SELECT COUNT(*) FROM sobe")
    suspend fun countAll(): Int
}